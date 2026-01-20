package com.incomingcallonly.launcher.data.repository

import com.incomingcallonly.launcher.data.local.CallLogDao
import com.incomingcallonly.launcher.data.local.ContactDao
import com.incomingcallonly.launcher.data.model.CallLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

import com.incomingcallonly.launcher.manager.EncryptionManager
import kotlinx.coroutines.flow.map

class CallLogRepositoryImpl @Inject constructor(
    private val callLogDao: CallLogDao,
    private val contactRepository: ContactRepository,
    private val encryptionManager: EncryptionManager
) : CallLogRepository {
    override fun getAllCallLogs(): Flow<List<CallLog>> =
        callLogDao.getAllCallLogs()
            .map { logs ->
                logs.map { log ->
                    log.copy(
                        name = log.name?.let { encryptionManager.decrypt(it) },
                        number = encryptionManager.decrypt(log.number)
                    )
                }
            }
            .combine(contactRepository.getAllContacts()) { logs, contacts ->
            logs.map { log ->
                val normalizedLogNumber = normalizeNumber(log.number)
                val contact = contacts.find { contact ->
                    val normalizedContactNumber = normalizeNumber(contact.phoneNumber)
                    // Check for exact normalized match or matching last 9 digits (handles +33 vs 06)
                    normalizedContactNumber == normalizedLogNumber ||
                            (normalizedLogNumber.length >= 9 && normalizedContactNumber.length >= 9 &&
                                    (normalizedLogNumber.endsWith(normalizedContactNumber.takeLast(9)) ||
                                            normalizedContactNumber.endsWith(
                                                normalizedLogNumber.takeLast(
                                                    9
                                                )
                                            )))
                }

                if (contact != null) {
                    log.copy(name = contact.name)
                } else {
                    log
                }
            }
        }

    private fun normalizeNumber(number: String): String {
        return number.replace(Regex("[^0-9+]"), "")
    }

    override suspend fun insertCallLog(callLog: CallLog) {
        val encryptedLog = callLog.copy(
            name = callLog.name?.let { encryptionManager.encrypt(it) },
            number = encryptionManager.encrypt(callLog.number)
        )
        callLogDao.insert(encryptedLog)
    }

    override suspend fun clearHistory() {
        callLogDao.clearAll()
    }
}

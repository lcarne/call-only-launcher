package com.incomingcallonly.launcher.data.repository

import android.content.Context
import android.util.Base64
import androidx.core.net.toUri
import com.incomingcallonly.launcher.data.local.ContactDao
import com.incomingcallonly.launcher.data.model.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import com.incomingcallonly.launcher.manager.EncryptionManager
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    @ApplicationContext private val context: Context,
    private val encryptionManager: EncryptionManager
) : ContactRepository {
    override fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()
        .map { contacts ->
            contacts.map { contact ->
                contact.copy(
                    name = encryptionManager.decrypt(contact.name),
                    phoneNumber = encryptionManager.decrypt(contact.phoneNumber)
                )
            }.sortedBy { it.name }
        }

    override suspend fun getContactsList(): List<Contact> = contactDao.getContactsList()
        .map { contact ->
            contact.copy(
                name = encryptionManager.decrypt(contact.name),
                phoneNumber = encryptionManager.decrypt(contact.phoneNumber)
            )
        }.sortedBy { it.name }

    override suspend fun getContactById(id: Int): Contact? {
        val contact = contactDao.getContactById(id) ?: return null
        return contact.copy(
            name = encryptionManager.decrypt(contact.name),
            phoneNumber = encryptionManager.decrypt(contact.phoneNumber)
        )
    }

    override suspend fun insertContact(contact: Contact) {
        val encryptedContact = contact.copy(
            name = encryptionManager.encrypt(contact.name),
            phoneNumber = encryptionManager.encrypt(contact.phoneNumber)
        )
        contactDao.insertContact(encryptedContact)
    }

    override suspend fun updateContact(contact: Contact) {
        val encryptedContact = contact.copy(
            name = encryptionManager.encrypt(contact.name),
            phoneNumber = encryptionManager.encrypt(contact.phoneNumber)
        )
        contactDao.updateContact(encryptedContact)
    }

    override suspend fun deleteContact(contact: Contact) = contactDao.deleteContact(contact)

    override suspend fun deleteAllContacts() = contactDao.deleteAllContacts()

    override suspend fun exportContacts(outputStream: OutputStream) {
        val contacts = getContactsList() // Uses decrypting getter
        outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
            contacts.forEach { contact ->
                writer.write("BEGIN:VCARD\n")
                writer.write("VERSION:3.0\n")
                writer.write("PRODID:-//Incoming Call Only Launcher//EN\n")

                // N Property format: FamilyName;GivenName;AdditionalName;Prefix;Suffix
                val nValue = formatNProperty(contact.name)
                writer.write("N:$nValue\n")

                // FN Property
                writer.write("FN:${escapeVCardValue(contact.name)}\n")

                // TEL Property
                val cleanedPhone = contact.phoneNumber.replace(Regex("[^0-9+]"), "")
                writer.write("TEL;TYPE=CELL:$cleanedPhone\n")

                // PHOTO Property
                contact.photoUri?.let { uriString ->
                    try {
                        val uri = uriString.toUri()
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            val bytes = inputStream.readBytes()
                            val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                            val photoLine = "PHOTO;ENCODING=b;TYPE=JPEG:$base64"
                            writer.write(foldVCardLine(photoLine))
                            writer.write("\n")
                        }
                    } catch (e: Exception) {
                        // Skip photo if failed
                    }
                }

                writer.write("END:VCARD\n\n")
            }
        }
    }

    private fun escapeVCardValue(value: String): String {
        return value.replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\n", "\\n")
    }

    private fun foldVCardLine(line: String): String {
        if (line.length <= 75) return line
        val result = StringBuilder()
        result.append(line.substring(0, 75))
        var index = 75
        while (index < line.length) {
            result.append("\n ")
            val limit = 74
            val chunk = if (index + limit <= line.length) {
                line.substring(index, index + limit)
            } else {
                line.substring(index)
            }
            result.append(chunk)
            index += limit
        }
        return result.toString()
    }

    private fun formatNProperty(fullName: String): String {
        val parts = fullName.trim().split(Regex("\\s+"))
        if (parts.isEmpty()) return ";;;;"

        var prefix = ""
        var given = ""
        var family = ""
        val additional = ""
        val suffix = ""

        val prefixes = listOf("M.", "Mme", "Mlle", "Dr.", "Pr.", "Mr.", "Ms.", "Mrs.")
        val remainingParts = parts.toMutableList()

        if (remainingParts.size > 1 && prefixes.any {
                it.equals(
                    remainingParts[0],
                    ignoreCase = true
                )
            }) {
            prefix = remainingParts.removeAt(0)
        }

        if (remainingParts.size >= 2) {
            family = remainingParts.removeAt(remainingParts.size - 1)
            given = remainingParts.joinToString(" ")
        } else if (remainingParts.size == 1) {
            family = remainingParts[0]
        }

        return "${escapeVCardValue(family)};${escapeVCardValue(given)};${escapeVCardValue(additional)};${
            escapeVCardValue(
                prefix
            )
        };${escapeVCardValue(suffix)}"
    }

    override suspend fun importContacts(inputStream: InputStream): Int {
        val reader = inputStream.bufferedReader()
        val lines = mutableListOf<String>()
        reader.forEachLine { line ->
            if (line.startsWith(" ") || line.startsWith("\t")) {
                if (lines.isNotEmpty()) {
                    lines[lines.size - 1] = lines.last() + line.substring(1)
                }
            } else {
                lines.add(line)
            }
        }

        var count = 0
        var name: String? = null
        val currentPhones = mutableListOf<Pair<String, String?>>()
        var photoBase64: String? = null

        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) -> {
                    name = null
                    currentPhones.clear()
                    photoBase64 = null
                }

                trimmed.startsWith("FN:", ignoreCase = true) -> {
                    name = trimmed.substring(3).trim()
                }

                trimmed.startsWith("TEL", ignoreCase = true) && trimmed.contains(":") -> {
                    val labelPart = trimmed.substringBefore(":")
                    // Normalize using PhoneNumberUtils to handle international formats (0033, +33, etc.)
                    val rawNumber = trimmed.substringAfter(":").trim()
                    val number = com.incomingcallonly.launcher.util.PhoneNumberUtils
                        .normalizePhoneNumber(rawNumber, context)

                    if (number.isNotBlank()) {
                        var type: String? = null
                        if (labelPart.contains("TYPE=", ignoreCase = true)) {
                            type = labelPart.substringAfter("TYPE=", "")
                                .substringBefore(";")
                                .substringBefore(",")
                                .trim()
                        } else if (labelPart.contains(";")) {
                            // Extract first attribute after TEL as type (e.g. TEL;CELL:...)
                            type = labelPart.substringAfter(";").substringBefore(";").trim()
                        }
                        currentPhones.add(number to type)
                    }
                }

                trimmed.startsWith("PHOTO", ignoreCase = true) && trimmed.contains(":") -> {
                    photoBase64 = trimmed.substringAfter(":").trim()
                }

                trimmed.startsWith("END:VCARD", ignoreCase = true) -> {
                    currentPhones.forEach { (phone, type) ->
                        // We need to check against decrypted contacts to find duplicates
                        val existing = getContactsList().find { it.phoneNumber == phone }
                        if (existing == null) {
                            var contactPhotoUri: String? = null
                            photoBase64?.let { base64 ->
                                try {
                                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                                    val fileName =
                                        "imported_contact_${System.currentTimeMillis()}_${count}.jpg"
                                    val photosDir = File(context.filesDir, "contact_photos")
                                    if (!photosDir.exists()) photosDir.mkdirs()
                                    val file = File(photosDir, fileName)
                                    FileOutputStream(file).use { it.write(bytes) }
                                    contactPhotoUri = android.net.Uri.fromFile(file).toString()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            val baseName = name ?: "Unknown"
                            val finalName = if (currentPhones.size > 1 && !type.isNullOrBlank()) {
                                "$baseName (${type.uppercase()})"
                            } else {
                                baseName
                            }

                            val newContact = Contact(
                                name = encryptionManager.encrypt(finalName),
                                phoneNumber = encryptionManager.encrypt(phone),
                                photoUri = contactPhotoUri
                            )
                            contactDao.insertContact(newContact)
                            count++
                        }
                    }
                }
            }
        }
        return count
    }

    override suspend fun getDeviceContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val uri = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER,
            android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIndex =
                cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex =
                cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER)
            val photoIndex =
                cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

            while (cursor.moveToNext()) {
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else "Unknown"
                val rawNumber = if (numberIndex != -1) cursor.getString(numberIndex) else ""
                val photoUri = if (photoIndex != -1) cursor.getString(photoIndex) else null

                if (rawNumber.isNotBlank()) {
                    // Normalize phone number to international format
                    val normalizedNumber = com.incomingcallonly.launcher.util.PhoneNumberUtils
                        .normalizePhoneNumber(rawNumber, context)

                    contacts.add(
                        Contact(
                            name = name ?: "Unknown",
                            phoneNumber = normalizedNumber,
                            photoUri = photoUri
                        )
                    )
                }
            }
        }
        return contacts.distinctBy { it.name + it.phoneNumber }
    }
}

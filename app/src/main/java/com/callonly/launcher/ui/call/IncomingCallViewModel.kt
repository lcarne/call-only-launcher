package com.callonly.launcher.ui.call

import android.telephony.PhoneNumberUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.model.Contact
import com.callonly.launcher.data.repository.ContactRepository
import com.callonly.launcher.manager.CallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IncomingCallViewModel @Inject constructor(
    private val callManager: CallManager,
    private val contactRepository: ContactRepository
) : ViewModel() {

    val incomingCallState = combine(
        callManager.incomingNumber,
        callManager.callState,
        contactRepository.getAllContacts()
    ) { number, state, contacts ->
        if (number == null) {
            IncomingCallUiState.Empty
        } else {
            val contact = contacts.find {
                @Suppress("DEPRECATION")
                PhoneNumberUtils.compare(it.phoneNumber, number)
            }
            when (state) {
                com.callonly.launcher.manager.CallState.Idle,
                com.callonly.launcher.manager.CallState.Ended -> IncomingCallUiState.Empty

                com.callonly.launcher.manager.CallState.Active -> IncomingCallUiState.Active(
                    number,
                    contact
                )

                com.callonly.launcher.manager.CallState.Ringing -> IncomingCallUiState.Ringing(
                    number,
                    contact
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomingCallUiState.Empty
    )

    init {
        // Initialization if needed
    }

    fun acceptCall() {
        callManager.accept()
    }

    fun rejectCall() {
        callManager.reject()
    }

    fun endCall() {
        callManager.reject() // reject() in CallManager already calls disconnect()
    }

    val isSpeakerOn = callManager.isSpeakerOn


    fun setSpeakerOn(on: Boolean) {
        callManager.setSpeakerOn(on)
    }
}

sealed class IncomingCallUiState {
    object Empty : IncomingCallUiState()
    data class Ringing(val number: String, val contact: Contact?) : IncomingCallUiState()
    data class Active(val number: String, val contact: Contact?) : IncomingCallUiState()
}

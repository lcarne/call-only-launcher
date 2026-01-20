package com.incomingcallonly.launcher.ui.admin

import androidx.lifecycle.ViewModel
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated = _isAuthenticated.asStateFlow()

    private val _pinError = MutableStateFlow(false)
    val pinError = _pinError.asStateFlow()

    val adminPin = settingsRepository.adminPin

    val lockoutEndTime = settingsRepository.lockoutEndTime
    val failedAttempts = settingsRepository.failedAttempts

    fun verifyPin(pin: String) {
        val currentTime = System.currentTimeMillis()
        if (currentTime < lockoutEndTime.value) {
            return
        }

        if (pin == adminPin.value) { // Use PIN from settings
            _isAuthenticated.value = true
            _pinError.value = false
            settingsRepository.resetFailedAttempts()
            settingsRepository.setLockoutEndTime(0)
        } else {
            _pinError.value = true
            settingsRepository.incrementFailedAttempts()
            if (settingsRepository.failedAttempts.value >= 5) {
                settingsRepository.setLockoutEndTime(currentTime + 30_000)
            }
        }
    }

    fun clearPinError() {
        _pinError.value = false
    }

    fun changePin(newPin: String) {
        settingsRepository.setAdminPin(newPin)
    }

    fun logout() {
        _isAuthenticated.value = false
    }
}

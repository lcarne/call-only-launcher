package com.callonly.launcher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.callonly.launcher.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: com.callonly.launcher.data.repository.SettingsRepository
) : ViewModel() {

    val isAlwaysOnEnabled = settingsRepository.isAlwaysOnEnabled
    val nightModeStartHour = settingsRepository.nightModeStartHour
    val nightModeEndHour = settingsRepository.nightModeEndHour
    val clockColor = settingsRepository.clockColor
    val isRingerEnabled = settingsRepository.isRingerEnabled
    val timeFormat = settingsRepository.timeFormat

    fun setRingerEnabled(enabled: Boolean) {
        settingsRepository.setRingerEnabled(enabled)
    }
}

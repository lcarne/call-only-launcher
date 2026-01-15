package com.incomingcallonly.launcher.ui.home.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import java.util.Date

private const val INACTIVITY_TIMEOUT_MS = 300L // Note: Preserving existing logic

data class InactivityState(
    private val _isDimmed: State<Boolean>,
    val onInteraction: () -> Unit
) {
    val isDimmed: Boolean get() = _isDimmed.value
}

@Composable
fun rememberInactivityState(
    currentBehavior: Int,
    isNight: Boolean,
    currentTime: Date
): InactivityState {
    val lastInteractionTime = remember { mutableLongStateOf(System.currentTimeMillis()) }
    val isDimmed = remember { mutableStateOf(false) }
    var previousIsNight by remember { mutableStateOf(isNight) }

    LaunchedEffect(currentTime, currentBehavior, isNight) {
        val isTransitioningFromNightToDay = previousIsNight && !isNight
        
        // Only monitor for dimming if we are in DIM mode and not in Night Mode
        if (currentBehavior == SettingsRepository.SCREEN_BEHAVIOR_DIM && !isNight) {
            // When transitioning from night to day in DIM mode, start dimmed immediately
            if (isTransitioningFromNightToDay) {
                isDimmed.value = true
            } else {
                val timeSinceLastInteraction = System.currentTimeMillis() - lastInteractionTime.longValue
                if (timeSinceLastInteraction > INACTIVITY_TIMEOUT_MS) {
                    isDimmed.value = true
                }
            }
        } else {
            isDimmed.value = false
        }
        
        previousIsNight = isNight
    }

    return remember {
        InactivityState(
            _isDimmed = isDimmed,
            onInteraction = {
                lastInteractionTime.longValue = System.currentTimeMillis()
                if (isDimmed.value) isDimmed.value = false
            }
        )
    }
}

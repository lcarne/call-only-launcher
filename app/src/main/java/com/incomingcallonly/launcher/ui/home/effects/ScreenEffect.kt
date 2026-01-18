package com.incomingcallonly.launcher.ui.home.effects

import android.content.Context
import android.view.WindowManager.LayoutParams
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.ui.home.HomeViewModel

private const val BRIGHTNESS_DIM = 0.01f

@Composable
fun ScreenEffect(
    viewModel: HomeViewModel,
    currentBehavior: Int,
    isNight: Boolean,
    isDimmed: Boolean
) {
    val context = LocalContext.current
    var previousIsNight by remember { mutableStateOf(isNight) }

    LaunchedEffect(
        currentBehavior,
        isNight,
        isDimmed
    ) { // removed currentTime as it triggers too often, check if needed
        val activity = context.findActivity()
        if (activity != null) {
            val isTransitioningFromNightToDay = previousIsNight && !isNight

            activity.window.clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)

            val params = activity.window.attributes

            if (!isNight) {
                if (isTransitioningFromNightToDay && currentBehavior != SettingsRepository.SCREEN_BEHAVIOR_OFF) {
                    viewModel.wakeUpScreen()
                }

                when (currentBehavior) {
                    SettingsRepository.SCREEN_BEHAVIOR_OFF -> {
                        // Standard Android timeout behavior
                        params.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    }

                    SettingsRepository.SCREEN_BEHAVIOR_DIM -> {
                        // Keep screen on
                        activity.window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
                        // When transitioning from night to day, or already dimmed, use low brightness
                        if (isTransitioningFromNightToDay || isDimmed) {
                            params.screenBrightness = BRIGHTNESS_DIM // Lowest brightness
                        } else {
                            params.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                        }
                    }

                    SettingsRepository.SCREEN_BEHAVIOR_AWAKE -> {
                        activity.window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
                        params.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                    }
                }
            } else {
                params.screenBrightness = LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            }
            activity.window.attributes = params

            previousIsNight = isNight
        }
    }
}

private fun Context.findActivity(): android.app.Activity? {
    var currentContext = this
    while (currentContext is android.content.ContextWrapper) {
        if (currentContext is android.app.Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

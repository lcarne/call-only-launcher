package com.incomingcallonly.launcher.ui.home.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.incomingcallonly.launcher.ui.home.HomeViewModel

@Composable
fun RingerEffect(
    viewModel: HomeViewModel,
    isNight: Boolean,
    isRingerEnabled: Boolean
) {
    var previousIsNightForRinger by remember { mutableStateOf(isNight) }

    LaunchedEffect(isNight) {
        if (isNight) {
            if (!previousIsNightForRinger) {
                viewModel.saveRingerStatePreNight(isRingerEnabled)
            }
            viewModel.setRingerEnabled(false)
        } else {
            if (previousIsNightForRinger) {
                viewModel.restoreRingerStatePreNight()
            }
        }
        previousIsNightForRinger = isNight
    }
}

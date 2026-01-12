package com.incomingcallonly.launcher.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.theme.DarkGray
import com.incomingcallonly.launcher.ui.theme.White

@Composable
fun RingerControl(
    isNight: Boolean,
    isRingerEnabled: Boolean,
    nightStart: Int,
    nightStartMin: Int,
    nightEnd: Int,
    nightEndMin: Int,
    accentColor: Color,
    timeFormat: String,
    onToggleRinger: () -> Unit
) {
    // Helper function to format time based on user preference
    fun formatTime(hour: Int, minute: Int): String {
        return if (timeFormat == "12") {
            val period = if (hour < 12) "AM" else "PM"
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format("%d:%02d %s", hour12, minute, period)
        } else {
            String.format("%02d:%02d", hour, minute)
        }
    }

    if (isNight) {
        // Night Mode Info - Read Only
        Surface(
            color = DarkGray,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_volume_off),
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.night_mode_active_ringer_off,
                        formatTime(nightStart, nightStartMin),
                        formatTime(nightEnd, nightEndMin)
                    ),
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Day Mode - Interactive Ringer Toggle with text
        Surface(
            color = if (isRingerEnabled) accentColor else DarkGray,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable { onToggleRinger() }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 48.dp, vertical = 24.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isRingerEnabled) R.drawable.ic_volume_up else R.drawable.ic_volume_off
                    ),
                    contentDescription = "Toggle Ringer",
                    tint = White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        if (isRingerEnabled) R.string.ringer_active else R.string.ringer_disabled
                    ),
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

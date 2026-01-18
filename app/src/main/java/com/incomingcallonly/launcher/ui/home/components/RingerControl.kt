package com.incomingcallonly.launcher.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.incomingcallonly.launcher.R
import com.incomingcallonly.launcher.ui.components.DepthIcon
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
    val is24Hour = timeFormat == "24"

    // Colors matching BatteryIcon in StatusComponents.kt

    val borderColor = Color(0xFF2A2A2A)

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
                DepthIcon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                    contentDescription = null,
                    tint = White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.night_mode_active_ringer_off,
                        com.incomingcallonly.launcher.util.TimeFormatUtils.formatTime(
                            nightStart,
                            nightStartMin,
                            is24Hour
                        ),
                        com.incomingcallonly.launcher.util.TimeFormatUtils.formatTime(
                            nightEnd,
                            nightEndMin,
                            is24Hour
                        )
                    ),
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    } else {
        // Day Mode - Interactive Ringer Toggle with Volume/Depth
        val backgroundColor = if (isRingerEnabled) accentColor else DarkGray
        val contentColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White

        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable { onToggleRinger() }
        ) {
            // Container with Volume Gradient
            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 48.dp, vertical = 24.dp)
                ) {
                    DepthIcon(
                        painter = if (isRingerEnabled) {
                            rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeUp)
                        } else {
                             rememberVectorPainter(Icons.AutoMirrored.Filled.VolumeOff)
                        },
                        contentDescription = "Toggle Ringer",
                        tint = contentColor,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(
                            if (isRingerEnabled) R.string.ringer_active else R.string.ringer_disabled
                        ),
                        color = contentColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

package com.incomingcallonly.launcher.util

import java.util.Locale

object TimeFormatUtils {
    fun formatTime(hour: Int, minute: Int, is24HourFormat: Boolean): String {
        return if (!is24HourFormat) {
            val period = if (hour < 12) "AM" else "PM"
            val hour12 = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            String.format(Locale.US, "%d:%02d %s", hour12, minute, period)
        } else {
            String.format(Locale.US, "%02dh%02d", hour, minute)
        }
    }
}

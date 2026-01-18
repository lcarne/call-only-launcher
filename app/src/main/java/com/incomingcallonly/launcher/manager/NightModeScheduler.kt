package com.incomingcallonly.launcher.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.incomingcallonly.launcher.receivers.NightModeEndReceiver
import java.util.Calendar

/**
 * Schedules an alarm to wake up the screen when night mode ends.
 */
object NightModeScheduler {

    private const val REQUEST_CODE_NIGHT_MODE_END = 1001

    /**
     * Schedule an alarm for when night mode ends.
     * Call this when night mode settings change or when the app starts.
     */
    fun scheduleNightModeEnd(context: Context) {
        val prefs = context.getSharedPreferences("incomingcallonly_prefs", Context.MODE_PRIVATE)

        // Check if night mode is enabled
        val isNightModeEnabled = prefs.getBoolean("night_mode_enabled", true)
        if (!isNightModeEnabled) {
            cancelNightModeEndAlarm(context)
            return
        }

        val endHour = prefs.getInt("night_mode_end", 7)
        val endMinute = prefs.getInt("night_mode_end_minute", 0)

        // Calculate the next occurrence of the end time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, endHour)
            set(Calendar.MINUTE, endMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If this time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NightModeEndReceiver::class.java).apply {
            action = NightModeEndReceiver.ACTION_NIGHT_MODE_END
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_NIGHT_MODE_END,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            // Use setExactAndAllowWhileIdle for reliable wake-up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // On Android 12+, check if we can schedule exact alarms
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // If exact alarm permission is not granted, use inexact alarm
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancel the night mode end alarm.
     */
    fun cancelNightModeEndAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NightModeEndReceiver::class.java).apply {
            action = NightModeEndReceiver.ACTION_NIGHT_MODE_END
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_NIGHT_MODE_END,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

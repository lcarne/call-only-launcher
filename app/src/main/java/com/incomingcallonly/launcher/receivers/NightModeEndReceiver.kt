package com.incomingcallonly.launcher.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.incomingcallonly.launcher.data.repository.SettingsRepository
import com.incomingcallonly.launcher.manager.NightModeScheduler

/**
 * BroadcastReceiver that wakes up the screen when night mode ends.
 * It checks the current screen behavior setting and wakes the screen accordingly.
 */
class NightModeEndReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_NIGHT_MODE_END = "com.incomingcallonly.launcher.ACTION_NIGHT_MODE_END"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_NIGHT_MODE_END) return

        // Get settings from SharedPreferences directly (receiver doesn't have DI)
        val prefs = context.getSharedPreferences("incomingcallonly_prefs", Context.MODE_PRIVATE)

        // Get current power state
        val isPlugged = isDevicePlugged(context)

        // Determine which behavior to use based on charging state
        val behavior = if (isPlugged) {
            prefs.getInt("screen_behavior_plugged", SettingsRepository.SCREEN_BEHAVIOR_AWAKE)
        } else {
            prefs.getInt("screen_behavior_battery", SettingsRepository.SCREEN_BEHAVIOR_OFF)
        }

        // If behavior is OFF, don't wake the screen
        if (behavior == SettingsRepository.SCREEN_BEHAVIOR_OFF) {
            // Still reschedule for next day
            NightModeScheduler.scheduleNightModeEnd(context)
            return
        }

        // Wake up the screen
        wakeUpScreen(context)

        // Reschedule for next day
        NightModeScheduler.scheduleNightModeEnd(context)
    }

    private fun isDevicePlugged(context: Context): Boolean {
        val intent = context.registerReceiver(
            null,
            android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val plugged = intent?.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        return plugged != 0
    }

    @Suppress("DEPRECATION")
    private fun wakeUpScreen(context: Context) {
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "IncomingCallOnly::NightModeEndWakeLock"
            )
            wakeLock.acquire(5000L) // 5 seconds timeout
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

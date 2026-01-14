package com.incomingcallonly.launcher.ui.home.effects

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberIsPlugged(): Boolean {
    val context = LocalContext.current
    var isPlugged by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val plugged = it.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, -1)
                    isPlugged = plugged == android.os.BatteryManager.BATTERY_PLUGGED_AC ||
                            plugged == android.os.BatteryManager.BATTERY_PLUGGED_USB ||
                            plugged == android.os.BatteryManager.BATTERY_PLUGGED_WIRELESS
                }
            }
        }
        val filter = android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = context.registerReceiver(receiver, filter)
        
        // Initial check
        intent?.let {
            val plugged = it.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, -1)
            isPlugged = plugged == android.os.BatteryManager.BATTERY_PLUGGED_AC ||
                    plugged == android.os.BatteryManager.BATTERY_PLUGGED_USB ||
                    plugged == android.os.BatteryManager.BATTERY_PLUGGED_WIRELESS
        }
        
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
    return isPlugged
}

package com.callonly.launcher.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class SimStatus {
    READY,
    LOCKED,
    ABSENT,
    UNKNOWN
}

@Singleton
class SimManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    private val _simStatus = MutableStateFlow(getSimStatusFromSystem())
    val simStatus: StateFlow<SimStatus> = _simStatus.asStateFlow()

    private val simStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "android.intent.action.SIM_STATE_CHANGED") {
                _simStatus.value = getSimStatusFromSystem()
            }
        }
    }

    init {
        val filter = IntentFilter("android.intent.action.SIM_STATE_CHANGED")
        context.registerReceiver(simStateReceiver, filter)
    }

    private fun getSimStatusFromSystem(): SimStatus {
        return when (telephonyManager.simState) {
            TelephonyManager.SIM_STATE_READY -> SimStatus.READY
            TelephonyManager.SIM_STATE_PIN_REQUIRED,
            TelephonyManager.SIM_STATE_PUK_REQUIRED,
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> SimStatus.LOCKED
            TelephonyManager.SIM_STATE_ABSENT -> SimStatus.ABSENT
            else -> SimStatus.UNKNOWN
        }
    }
}

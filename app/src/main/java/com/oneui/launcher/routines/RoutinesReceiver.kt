package com.oneui.launcher.routines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.BatteryManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RoutinesReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val storage = RoutinesStorage(context)
        val routines = storage.getRoutines().filter { it.isEnabled }

        if (routines.isEmpty()) return

        when (intent.action) {
            Intent.ACTION_TIME_TICK -> {
                // Check TIME routines
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val currentTime = sdf.format(Calendar.getInstance().time)
                for (routine in routines) {
                    if (routine.triggerType == TriggerType.TIME && routine.triggerValue == currentTime) {
                        RoutineActionExecutor.execute(context, routine.actionType, routine.actionValue)
                    }
                }
            }

            "android.net.wifi.STATE_CHANGE" -> {
                val wifiInfo = intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO)
                if (wifiInfo != null && wifiInfo.isConnected) {
                    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val activeSsid = wifiManager.connectionInfo.ssid?.replace("\"", "") ?: ""
                    for (routine in routines) {
                        if (routine.triggerType == TriggerType.WIFI && routine.triggerValue.equals(activeSsid, ignoreCase = true)) {
                            RoutineActionExecutor.execute(context, routine.actionType, routine.actionValue)
                        }
                    }
                }
            }

            Intent.ACTION_BATTERY_CHANGED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val pct = (level * 100 / scale.toFloat()).toInt()
                for (routine in routines) {
                    if (routine.triggerType == TriggerType.BATTERY) {
                        val triggerPct = routine.triggerValue.toIntOrNull() ?: -1
                        if (triggerPct != -1 && pct == triggerPct) {
                            RoutineActionExecutor.execute(context, routine.actionType, routine.actionValue)
                        }
                    }
                }
            }
        }
    }
}

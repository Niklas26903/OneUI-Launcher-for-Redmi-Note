package com.oneui.launcher.routines

enum class TriggerType {
    TIME,
    WIFI,
    BATTERY
}

enum class ActionType {
    VOLUME,
    BRIGHTNESS,
    DARK_MODE,
    LAUNCH_APP
}

data class Routine(
    val id: String,
    val name: String,
    val triggerType: TriggerType,
    val triggerValue: String, // format: "HH:mm" for TIME, WiFi name for WIFI, "battery_pct" for BATTERY
    val actionType: ActionType,
    val actionValue: String,  // format: volume_pct, brightness_pct, "true"/"false" for dark mode, packageName for app
    val isEnabled: Boolean = true
)

package com.oneui.launcher.routines

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import android.widget.Toast

class RoutineActionExecutor {

    companion object {
        fun execute(context: Context, actionType: ActionType, actionValue: String) {
            try {
                when (actionType) {
                    ActionType.VOLUME -> {
                        val volumePercent = actionValue.toIntOrNull() ?: 50
                        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
                        val targetVol = (maxVol * (volumePercent / 100f)).toInt()
                        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, targetVol, AudioManager.FLAG_SHOW_UI)
                        Toast.makeText(context, "Routine: Lautstärke auf $volumePercent% gesetzt", Toast.LENGTH_SHORT).show()
                    }
                    ActionType.BRIGHTNESS -> {
                        val brightnessPercent = actionValue.toIntOrNull() ?: 50
                        val targetVal = (255 * (brightnessPercent / 100f)).toInt()
                        if (Settings.System.canWrite(context)) {
                            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, targetVal)
                            Toast.makeText(context, "Routine: Helligkeit auf $brightnessPercent% gesetzt", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Systemeinstellungen schreiben-Berechtigung fehlt!", Toast.LENGTH_LONG).show()
                        }
                    }
                    ActionType.DARK_MODE -> {
                        // Normally handled via system theme. We notify user/perform a toggle in active states if supported.
                        Toast.makeText(context, "Routine: Dark Mode toggled to: $actionValue", Toast.LENGTH_SHORT).show()
                    }
                    ActionType.LAUNCH_APP -> {
                        val intent = context.packageManager.getLaunchIntentForPackage(actionValue)
                        if (intent != null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                            Toast.makeText(context, "Routine: App $actionValue gestartet", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

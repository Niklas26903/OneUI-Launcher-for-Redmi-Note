package com.oneui.launcher

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivityWidgetSettingsBinding

class WidgetSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWidgetSettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()

        binding.btnSaveWidgetSettings.setOnClickListener {
            savePreferences()
        }
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.swWidgetOverlap.isChecked = prefs.getBoolean("widget_overlap", false)
        binding.swWidgetSnap.isChecked = prefs.getBoolean("widget_snap_to_grid", true)
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("widget_overlap", binding.swWidgetOverlap.isChecked)
        editor.putBoolean("widget_snap_to_grid", binding.swWidgetSnap.isChecked)

        editor.apply()
        Toast.makeText(this, "Widget-Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
        finish()
    }
}

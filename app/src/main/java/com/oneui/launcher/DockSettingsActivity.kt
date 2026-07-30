package com.oneui.launcher

import android.content.Context
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivityDockSettingsBinding

class DockSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDockSettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDockSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()
        setupListeners()
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val dockCount = prefs.getInt("dock_icon_count", 4)
        binding.sbDockCount.progress = dockCount - 1
        binding.tvDockCountLabel.text = "$dockCount Symbole"

        binding.swDockGlass.isChecked = prefs.getBoolean("dock_glass_effect", true)
        binding.etDockBgColor.setText(prefs.getString("dock_bg_color", "#22000000"))
        binding.sbDockTransparency.progress = prefs.getInt("dock_bg_transparency", 15)
        binding.etDockCornerRadius.setText(prefs.getInt("dock_corner_radius", 16).toString())
        binding.swDockAutohide.isChecked = prefs.getBoolean("dock_auto_hide", false)
    }

    private fun setupListeners() {
        binding.sbDockCount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvDockCountLabel.text = "${progress + 1} Symbole"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSaveDockSettings.setOnClickListener {
            savePreferences()
        }
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            editor.putInt("dock_icon_count", binding.sbDockCount.progress + 1)
            editor.putBoolean("dock_glass_effect", binding.swDockGlass.isChecked)
            editor.putString("dock_bg_color", binding.etDockBgColor.text.toString())
            editor.putInt("dock_bg_transparency", binding.sbDockTransparency.progress)
            editor.putInt("dock_corner_radius", binding.etDockCornerRadius.text.toString().toInt())
            editor.putBoolean("dock_auto_hide", binding.swDockAutohide.isChecked)

            editor.apply()
            Toast.makeText(this, "Dock-Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Fehler: Ungültige Eingabe!", Toast.LENGTH_SHORT).show()
        }
    }
}

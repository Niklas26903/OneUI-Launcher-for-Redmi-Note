package com.oneui.launcher

import android.content.Context
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivityIconSettingsBinding

class IconSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIconSettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIconSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()
        setupListeners()
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val sizePct = prefs.getInt("icon_size", 100) // 50% to 150%, offset is 50
        binding.sbIconSize.progress = sizePct - 50
        binding.tvIconSizeLabel.text = "$sizePct%"

        binding.swShowLabels.isChecked = prefs.getBoolean("show_icon_labels", true)

        val maxLines = prefs.getInt("icon_label_max_lines", 1)
        if (maxLines == 1) {
            binding.rbLines1.isChecked = true
        } else {
            binding.rbLines2.isChecked = true
        }

        binding.etLabelSize.setText(prefs.getInt("icon_label_size", 12).toString())
        binding.etLabelColor.setText(prefs.getString("icon_label_color", "#FFFFFF"))
        binding.etIconPack.setText(prefs.getString("icon_pack_package", "default"))
    }

    private fun setupListeners() {
        binding.sbIconSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvIconSizeLabel.text = "${progress + 50}%"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSaveIconSettings.setOnClickListener {
            savePreferences()
        }
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            editor.putInt("icon_size", binding.sbIconSize.progress + 50)
            editor.putBoolean("show_icon_labels", binding.swShowLabels.isChecked)

            val maxLines = if (binding.rbLines1.isChecked) 1 else 2
            editor.putInt("icon_label_max_lines", maxLines)

            editor.putInt("icon_label_size", binding.etLabelSize.text.toString().toInt())
            editor.putString("icon_label_color", binding.etLabelColor.text.toString())
            editor.putString("icon_pack_package", binding.etIconPack.text.toString())

            editor.apply()
            Toast.makeText(this, "Symbol-Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Fehler: Ungültige Eingabe!", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.oneui.launcher

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivityFolderSettingsBinding

class FolderSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFolderSettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        loadPreferences()

        binding.btnSaveFolderSettings.setOnClickListener {
            savePreferences()
        }
    }

    private fun setupSpinners() {
        val shapes = listOf("Rund", "Quadratisch", "Squircle", "Eigene Form")
        binding.spFolderShape.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, shapes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val anims = listOf("Fade", "Scale", "Slide")
        binding.spFolderAnim.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anims).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val fonts = listOf("Default", "Sans-Serif", "Serif", "Monospace")
        binding.spFolderFont.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fonts).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.etFolderSize.setText(prefs.getInt("folder_size", 64).toString())
        binding.etFolderMiniGrid.setText(prefs.getString("folder_mini_grid", "3x3"))
        binding.etFolderBg.setText(prefs.getString("folder_bg_color", "#FFFFFF"))
        binding.sbFolderTransparency.progress = prefs.getInt("folder_transparency", 30)

        val shapeIndex = when (prefs.getString("folder_shape", "squircle")) {
            "round" -> 0
            "square" -> 1
            "squircle" -> 2
            "custom" -> 3
            else -> 2
        }
        binding.spFolderShape.setSelection(shapeIndex)

        binding.etFolderBorderColor.setText(prefs.getString("folder_border_color", "#E0E0E0"))
        binding.etFolderBorderWidth.setText(prefs.getInt("folder_border_width", 2).toString())

        val animIndex = when (prefs.getString("folder_anim_style", "scale")) {
            "fade" -> 0
            "scale" -> 1
            "slide" -> 2
            else -> 1
        }
        binding.spFolderAnim.setSelection(animIndex)

        binding.sbFolderWidth.progress = prefs.getInt("folder_window_width", 85)
        binding.sbFolderHeight.progress = prefs.getInt("folder_window_height", 60)
        binding.sbFolderDim.progress = prefs.getInt("folder_bg_dim", 50)
        binding.etFolderOpenGrid.setText(prefs.getString("folder_grid", "3x3"))

        val fontIndex = when (prefs.getString("folder_font_style", "default")) {
            "default" -> 0
            "sans-serif" -> 1
            "serif" -> 2
            "monospace" -> 3
            else -> 0
        }
        binding.spFolderFont.setSelection(fontIndex)
        binding.etFolderTextColor.setText(prefs.getString("folder_text_color", "#1D1D1F"))
    }

    private fun savePreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        try {
            editor.putInt("folder_size", binding.etFolderSize.text.toString().toInt())
            editor.putString("folder_mini_grid", binding.etFolderMiniGrid.text.toString())
            editor.putString("folder_bg_color", binding.etFolderBg.text.toString())
            editor.putInt("folder_transparency", binding.sbFolderTransparency.progress)

            val shape = when (binding.spFolderShape.selectedItemPosition) {
                0 -> "round"
                1 -> "square"
                2 -> "squircle"
                3 -> "custom"
                else -> "squircle"
            }
            editor.putString("folder_shape", shape)

            editor.putString("folder_border_color", binding.etFolderBorderColor.text.toString())
            editor.putInt("folder_border_width", binding.etFolderBorderWidth.text.toString().toInt())

            val anim = when (binding.spFolderAnim.selectedItemPosition) {
                0 -> "fade"
                1 -> "scale"
                2 -> "slide"
                else -> "scale"
            }
            editor.putString("folder_anim_style", anim)

            editor.putInt("folder_window_width", binding.sbFolderWidth.progress)
            editor.putInt("folder_window_height", binding.sbFolderHeight.progress)
            editor.putInt("folder_bg_dim", binding.sbFolderDim.progress)
            editor.putString("folder_grid", binding.etFolderOpenGrid.text.toString())

            val font = when (binding.spFolderFont.selectedItemPosition) {
                0 -> "default"
                1 -> "sans-serif"
                2 -> "serif"
                3 -> "monospace"
                else -> "default"
            }
            editor.putString("folder_font_style", font)
            editor.putString("folder_text_color", binding.etFolderTextColor.text.toString())

            editor.apply()
            Toast.makeText(this, "Ordner-Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Fehler: Ungültige Eingabe!", Toast.LENGTH_SHORT).show()
        }
    }
}

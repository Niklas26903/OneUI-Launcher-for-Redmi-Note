package com.oneui.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()
        setupListeners()
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Workspace setup
        val isWorkspaceHoriz = prefs.getBoolean("workspace_horizontal", true)
        if (isWorkspaceHoriz) {
            binding.rbWorkspaceHoriz.isChecked = true
        } else {
            binding.rbWorkspaceVert.isChecked = true
        }
        binding.etWorkspaceGrid.setText(prefs.getString("workspace_grid_string", "4x5"))

        // Drawer setup
        val isDrawerHoriz = prefs.getBoolean("drawer_horizontal", false)
        if (isDrawerHoriz) {
            binding.rbDrawerHoriz.isChecked = true
        } else {
            binding.rbDrawerVert.isChecked = true
        }
        binding.etDrawerGrid.setText(prefs.getString("drawer_grid_string", "4x5"))
    }

    private fun setupListeners() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Navigate to Folder Settings
        binding.btnFolderSettings.setOnClickListener {
            startActivity(Intent(this, FolderSettingsActivity::class.java))
        }

        // Navigate to Dock Settings
        binding.btnDockSettings.setOnClickListener {
            startActivity(Intent(this, DockSettingsActivity::class.java))
        }

        // Navigate to Icon Settings
        binding.btnIconSettings.setOnClickListener {
            startActivity(Intent(this, IconSettingsActivity::class.java))
        }

        // Navigate to Widget Settings
        binding.btnWidgetSettings.setOnClickListener {
            startActivity(Intent(this, WidgetSettingsActivity::class.java))
        }

        // Save main grid and layout configs
        binding.btnSaveSettings.setOnClickListener {
            val editor = prefs.edit()
            val workspaceGrid = binding.etWorkspaceGrid.text.toString().trim()
            val drawerGrid = binding.etDrawerGrid.text.toString().trim()

            if (!validateGrid(workspaceGrid) || !validateGrid(drawerGrid)) {
                Toast.makeText(this, "Ungültiges Raster-Format! Erwartet: ZeilenxSpalten (z.B. 4x5)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            editor.putBoolean("workspace_horizontal", binding.rbWorkspaceHoriz.isChecked)
            editor.putString("workspace_grid_string", workspaceGrid)

            editor.putBoolean("drawer_horizontal", binding.rbDrawerHoriz.isChecked)
            editor.putString("drawer_grid_string", drawerGrid)

            editor.apply()
            Toast.makeText(this, "Gitter- & Layout-Einstellungen gespeichert!", Toast.LENGTH_SHORT).show()
        }

        // Launch Routine+ Standalone app
        binding.btnGoRoutines.setOnClickListener {
            val pm = packageManager
            val intent = pm.getLaunchIntentForPackage("com.oneui.routineplus")
            if (intent != null) {
                startActivity(intent)
            } else {
                startActivity(Intent(this, RoutinesActivity::class.java))
            }
        }
    }

    private fun validateGrid(grid: String): Boolean {
        val parts = grid.split("x")
        if (parts.size != 2) return false
        val rows = parts[0].toIntOrNull()
        val cols = parts[1].toIntOrNull()
        return rows != null && cols != null && rows > 0 && cols > 0
    }
}

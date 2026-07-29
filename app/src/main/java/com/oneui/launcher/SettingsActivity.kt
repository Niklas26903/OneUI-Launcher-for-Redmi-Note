package com.oneui.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.oneui.launcher.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val PREFS_NAME = "OneUI_Prefs"
    private val KEY_WORKSPACE_GRID = "workspace_grid_size"
    private val KEY_DRAWER_GRID = "drawer_grid_size"
    private val KEY_DRAWER_VERTICAL = "drawer_vertical"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadPreferences()
        setupListeners()
    }

    private fun loadPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val workspaceGrid = prefs.getInt(KEY_WORKSPACE_GRID, 4)
        val drawerGrid = prefs.getInt(KEY_DRAWER_GRID, 4)
        val isVertical = prefs.getBoolean(KEY_DRAWER_VERTICAL, true)

        // Select proper workspace radio button
        when (workspaceGrid) {
            4 -> binding.rbW4x5.isChecked = true
            5 -> binding.rbW5x5.isChecked = true
            else -> binding.rbW4x5.isChecked = true
        }

        // Select proper drawer radio button
        when (drawerGrid) {
            4 -> binding.rbD4x5.isChecked = true
            5 -> binding.rbD5x5.isChecked = true
            else -> binding.rbD4x5.isChecked = true
        }

        if (isVertical) {
            binding.rbDirVertical.isChecked = true
        } else {
            binding.rbDirHorizontal.isChecked = true
        }
    }

    private fun setupListeners() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        binding.rgWorkspaceGrid.setOnCheckedChangeListener { _, checkedId ->
            val cols = when (checkedId) {
                R.id.rb_w_4x5 -> 4
                R.id.rb_w_4x6 -> 4
                R.id.rb_w_5x5 -> 5
                R.id.rb_w_5x6 -> 5
                else -> 4
            }
            prefs.edit().putInt(KEY_WORKSPACE_GRID, cols).apply()
        }

        binding.rgDrawerGrid.setOnCheckedChangeListener { _, checkedId ->
            val cols = when (checkedId) {
                R.id.rb_d_4x5 -> 4
                R.id.rb_d_4x6 -> 4
                R.id.rb_d_5x5 -> 5
                R.id.rb_d_5x6 -> 5
                else -> 4
            }
            prefs.edit().putInt(KEY_DRAWER_GRID, cols).apply()
        }

        binding.rgDrawerDirection.setOnCheckedChangeListener { _, checkedId ->
            val isVertical = checkedId == R.id.rb_dir_vertical
            prefs.edit().putBoolean(KEY_DRAWER_VERTICAL, isVertical).apply()
        }

        binding.btnGoRoutines.setOnClickListener {
            startActivity(Intent(this, RoutinesActivity::class.java))
        }
    }
}

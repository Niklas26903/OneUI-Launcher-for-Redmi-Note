package com.oneui.launcher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oneui.launcher.adapters.AppDrawerAdapter
import com.oneui.launcher.databinding.ActivityMainBinding
import com.oneui.launcher.models.AppInfo
import com.oneui.launcher.utils.OnSwipeListener

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var allApps: List<AppInfo> = emptyList()
    private lateinit var workspaceAdapter: AppDrawerAdapter
    private lateinit var drawerAdapter: AppDrawerAdapter

    // Settings preferences keys
    private val PREFS_NAME = "OneUI_Prefs"
    private val KEY_WORKSPACE_GRID = "workspace_grid_size"
    private val KEY_DRAWER_GRID = "drawer_grid_size"
    private val KEY_DRAWER_VERTICAL = "drawer_vertical"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadInstalledApps()
        setupWorkspace()
        setupAppDrawer()
        setupGestures()

        // Handle settings navigation
        binding.btnLauncherSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload settings configuration in case grid size or drawer alignment was changed.
        applyGridPreferences()
    }

    private fun loadInstalledApps() {
        val pm = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            pm.queryIntentActivities(intent, 0)
        }

        allApps = resolveInfos.map { info ->
            AppInfo(
                label = info.loadLabel(pm),
                packageName = info.activityInfo.packageName,
                icon = info.loadIcon(pm),
                componentName = info.activityInfo.name
            )
        }.sortedBy { it.label.toString().lowercase() }
    }

    private fun setupWorkspace() {
        // Workspace grid has 4 persistent or newly selected favorites
        val favorites = allApps.take(8)
        workspaceAdapter = AppDrawerAdapter(favorites, isWhiteText = true) { app ->
            launchApp(app)
        }

        binding.workspaceGrid.adapter = workspaceAdapter
        applyGridPreferences()

        // Setup persistent app bottom dock (typically 4-5 apps)
        binding.bottomDock.removeAllViews()
        val dockApps = allApps.take(4)
        for (app in dockApps) {
            val appView = layoutInflater.inflate(R.layout.item_app, binding.bottomDock, false)
            val iconView = appView.findViewById<android.widget.ImageView>(R.id.app_icon)
            val labelView = appView.findViewById<android.widget.TextView>(R.id.app_label)
            iconView.setImageDrawable(app.icon)
            labelView.text = app.label
            labelView.setTextColor(android.graphics.Color.WHITE)
            appView.setOnClickListener {
                launchApp(app)
            }
            // Add custom weighting
            val params = android.widget.LinearLayout.LayoutParams(
                0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            )
            appView.layoutParams = params
            binding.bottomDock.addView(appView)
        }
    }

    private fun setupAppDrawer() {
        drawerAdapter = AppDrawerAdapter(allApps, isWhiteText = false) { app ->
            launchApp(app)
            hideAppDrawer()
        }
        binding.appsRecycler.adapter = drawerAdapter

        // Search input logic
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterApps(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.label.toString().contains(query, ignoreCase = true) }
        }
        drawerAdapter.updateData(filtered)
    }

    private fun applyGridPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val workspaceGrid = prefs.getInt(KEY_WORKSPACE_GRID, 4) // Default is 4 columns
        val drawerGrid = prefs.getInt(KEY_DRAWER_GRID, 4)
        val isVertical = prefs.getBoolean(KEY_DRAWER_VERTICAL, true)

        binding.workspaceGrid.layoutManager = GridLayoutManager(this, workspaceGrid)

        val drawerLayoutManager = if (isVertical) {
            GridLayoutManager(this, drawerGrid)
        } else {
            // Horizontal scrolling layout manager setup
            GridLayoutManager(this, drawerGrid, GridLayoutManager.HORIZONTAL, false)
        }
        binding.appsRecycler.layoutManager = drawerLayoutManager
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestures() {
        val swipeListener = object : OnSwipeListener(this) {
            override fun onSwipeUp() {
                showAppDrawer()
            }

            override fun onSwipeDown() {
                // Pull down status bar/notification panel (typical Android 11+ feature)
                expandNotificationPanel()
            }

            override fun onDoubleTap() {
                vibratePhone()
                // In actual unrooted devices, screen locking requires device administration API.
                // We perform visual feedback & standard vibration.
            }
        }

        binding.mainRoot.setOnTouchListener(swipeListener)
        // Set swipe listener on the workspace as well
        binding.workspaceGrid.setOnTouchListener(swipeListener)
    }

    private fun showAppDrawer() {
        binding.appDrawerContainer.visibility = View.VISIBLE
        binding.appDrawerContainer.alpha = 0f
        binding.appDrawerContainer.animate().alpha(1f).setDuration(250).start()
        binding.searchInput.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideAppDrawer() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
        binding.appDrawerContainer.animate().alpha(0f).setDuration(200).withEndAction {
            binding.appDrawerContainer.visibility = View.GONE
            binding.searchInput.setText("")
        }.start()
    }

    private fun launchApp(app: AppInfo) {
        val intent = packageManager.getLaunchIntentForPackage(app.packageName.toString())
        if (intent != null) {
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (binding.appDrawerContainer.visibility == View.VISIBLE) {
            hideAppDrawer()
        } else {
            super.onBackPressed()
        }
    }

    private fun expandNotificationPanel() {
        try {
            @SuppressLint("WrongConstant")
            val statusBarService = getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expandMethod = statusBarManager.getMethod("expandNotificationsPanel")
            expandMethod.invoke(statusBarService)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
}

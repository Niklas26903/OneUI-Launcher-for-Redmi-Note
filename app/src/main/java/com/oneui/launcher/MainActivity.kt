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

    private var workspaceSnapHelper: androidx.recyclerview.widget.PagerSnapHelper? = null
    private var drawerSnapHelper: androidx.recyclerview.widget.PagerSnapHelper? = null

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
        applyDockPreferences()
        if (::workspaceAdapter.isInitialized) {
            workspaceAdapter.notifyDataSetChanged()
        }
        if (::drawerAdapter.isInitialized) {
            drawerAdapter.notifyDataSetChanged()
        }
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
        val placeholderIcon = if (allApps.isNotEmpty()) allApps.first().icon else resources.getDrawable(android.R.drawable.sym_def_app_icon, null)
        val folderGoogle = AppInfo(
            label = "Google Apps",
            packageName = "folder:google",
            icon = placeholderIcon,
            componentName = ""
        )
        val folderTools = AppInfo(
            label = "System Tools",
            packageName = "folder:tools",
            icon = placeholderIcon,
            componentName = ""
        )
        // Workspace grid has folders plus some favorites
        val favorites = listOf(folderGoogle, folderTools) + allApps.take(6)
        workspaceAdapter = AppDrawerAdapter(favorites, isWhiteText = true) { app ->
            if (app.packageName.toString().startsWith("folder:")) {
                openFolder(app)
            } else {
                launchApp(app)
            }
        }

        binding.workspaceGrid.adapter = workspaceAdapter
        applyGridPreferences()
        applyDockPreferences()
    }

    private fun setupAppDrawer() {
        val placeholderIcon = if (allApps.isNotEmpty()) allApps.first().icon else resources.getDrawable(android.R.drawable.sym_def_app_icon, null)
        val folderGoogle = AppInfo(
            label = "Google Apps",
            packageName = "folder:google",
            icon = placeholderIcon,
            componentName = ""
        )
        val folderTools = AppInfo(
            label = "System Tools",
            packageName = "folder:tools",
            icon = placeholderIcon,
            componentName = ""
        )
        val allAppsWithFolders = listOf(folderGoogle, folderTools) + allApps
        drawerAdapter = AppDrawerAdapter(allAppsWithFolders, isWhiteText = false) { app ->
            if (app.packageName.toString().startsWith("folder:")) {
                openFolder(app)
            } else {
                launchApp(app)
                hideAppDrawer()
            }
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

    private fun parseGridString(gridStr: String, defaultRows: Int, defaultCols: Int): Pair<Int, Int> {
        val parts = gridStr.split("x")
        if (parts.size != 2) return Pair(defaultRows, defaultCols)
        val rows = parts[0].toIntOrNull() ?: defaultRows
        val cols = parts[1].toIntOrNull() ?: defaultCols
        return Pair(rows, cols)
    }

    private fun attachSnapHelper(recyclerView: RecyclerView, snapHelper: androidx.recyclerview.widget.PagerSnapHelper) {
        try {
            snapHelper.attachToRecyclerView(recyclerView)
        } catch (e: Exception) {
            // Already attached
        }
    }

    private fun detachSnapHelper(snapHelper: androidx.recyclerview.widget.PagerSnapHelper) {
        try {
            snapHelper.attachToRecyclerView(null)
        } catch (e: Exception) {
            // Not attached
        }
    }

    private fun applyGridPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Workspace Gitter & Scrollrichtung
        val workspaceGridStr = prefs.getString("workspace_grid_string", "4x5") ?: "4x5"
        val (wRows, wCols) = parseGridString(workspaceGridStr, 4, 5)
        val isWHoriz = prefs.getBoolean("workspace_horizontal", true)

        if (isWHoriz) {
            binding.workspaceGrid.layoutManager = GridLayoutManager(this, wRows, GridLayoutManager.HORIZONTAL, false)
            if (workspaceSnapHelper == null) {
                workspaceSnapHelper = androidx.recyclerview.widget.PagerSnapHelper()
            }
            detachSnapHelper(workspaceSnapHelper!!)
            attachSnapHelper(binding.workspaceGrid, workspaceSnapHelper!!)
        } else {
            binding.workspaceGrid.layoutManager = GridLayoutManager(this, wCols, GridLayoutManager.VERTICAL, false)
            workspaceSnapHelper?.let { detachSnapHelper(it) }
        }

        // Drawer Gitter & Scrollrichtung
        val drawerGridStr = prefs.getString("drawer_grid_string", "4x5") ?: "4x5"
        val (dRows, dCols) = parseGridString(drawerGridStr, 4, 5)
        val isDHoriz = prefs.getBoolean("drawer_horizontal", false)

        if (isDHoriz) {
            binding.appsRecycler.layoutManager = GridLayoutManager(this, dRows, GridLayoutManager.HORIZONTAL, false)
            if (drawerSnapHelper == null) {
                drawerSnapHelper = androidx.recyclerview.widget.PagerSnapHelper()
            }
            detachSnapHelper(drawerSnapHelper!!)
            attachSnapHelper(binding.appsRecycler, drawerSnapHelper!!)
        } else {
            binding.appsRecycler.layoutManager = GridLayoutManager(this, dCols, GridLayoutManager.VERTICAL, false)
            drawerSnapHelper?.let { detachSnapHelper(it) }
        }
    }

    private fun applyDockPreferences() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dockCount = prefs.getInt("dock_icon_count", 4)
        val showGlass = prefs.getBoolean("dock_glass_effect", true)
        val bgColorStr = prefs.getString("dock_bg_color", "#22000000") ?: "#22000000"
        val transparency = prefs.getInt("dock_bg_transparency", 15)
        val cornerRadius = prefs.getInt("dock_corner_radius", 16)
        val autoHide = prefs.getBoolean("dock_auto_hide", false)

        // 1. Populate icons dynamically from dockCount (1-9)
        binding.bottomDock.removeAllViews()
        val dockApps = allApps.take(dockCount)
        val density = resources.displayMetrics.density

        // Programmatic scaling of icons based on icon_size
        val iconSizePct = prefs.getInt("icon_size", 100)
        val baseSizePx = (50 * density).toInt()
        val scaledSizePx = (baseSizePx * (iconSizePct / 100f)).toInt()

        for (app in dockApps) {
            val appView = layoutInflater.inflate(R.layout.item_app, binding.bottomDock, false)
            val iconView = appView.findViewById<android.widget.ImageView>(R.id.app_icon)
            val labelView = appView.findViewById<android.widget.TextView>(R.id.app_label)

            iconView.setImageDrawable(app.icon)
            iconView.layoutParams.width = scaledSizePx
            iconView.layoutParams.height = scaledSizePx

            // Text Under Dock Icons: obey visibility and text style
            val showLabels = prefs.getBoolean("show_icon_labels", true)
            if (showLabels) {
                labelView.visibility = View.VISIBLE
                labelView.text = app.label
                labelView.setTextColor(android.graphics.Color.WHITE)
                val labelSize = prefs.getInt("icon_label_size", 12)
                labelView.textSize = labelSize.toFloat()
            } else {
                labelView.visibility = View.GONE
            }

            appView.setOnClickListener {
                launchApp(app)
            }
            val params = android.widget.LinearLayout.LayoutParams(
                0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
            )
            appView.layoutParams = params
            binding.bottomDock.addView(appView)
        }

        // 2. Programmatic background styling
        val bgDrawable = android.graphics.drawable.GradientDrawable()
        bgDrawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE
        bgDrawable.cornerRadius = cornerRadius * density

        val parsedColor = try { android.graphics.Color.parseColor(bgColorStr) } catch(e: Exception) { android.graphics.Color.parseColor("#22000000") }
        val alpha = ((100 - transparency) * 2.55).toInt().coerceIn(0, 255)

        bgDrawable.setColor(android.graphics.Color.argb(
            alpha,
            android.graphics.Color.red(parsedColor),
            android.graphics.Color.green(parsedColor),
            android.graphics.Color.blue(parsedColor)
        ))

        binding.bottomDock.background = bgDrawable

        // 3. Auto-hide options (hide when app drawer open / show otherwise)
        if (autoHide && binding.appDrawerContainer.visibility == View.VISIBLE) {
            binding.bottomDock.visibility = View.GONE
        } else {
            binding.bottomDock.visibility = View.VISIBLE
        }
    }

    private fun openFolder(folder: AppInfo) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)

        val dialogView = layoutInflater.inflate(R.layout.dialog_folder_open, null)
        dialog.setContentView(dialogView)

        // Window background transparent
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))

        // Custom Dimming
        val dimPct = prefs.getInt("folder_bg_dim", 50)
        dialog.window?.setDimAmount(dimPct / 100f)

        val card = dialogView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.folder_card)
        val titleView = dialogView.findViewById<android.widget.TextView>(R.id.tv_folder_title)
        val rvApps = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_folder_apps)

        // Title & style
        titleView.text = folder.label
        val fontStyle = prefs.getString("folder_font_style", "default")
        val typeface = when (fontStyle) {
            "sans-serif" -> android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
            "serif" -> android.graphics.Typeface.create("serif", android.graphics.Typeface.BOLD)
            "monospace" -> android.graphics.Typeface.create("monospace", android.graphics.Typeface.BOLD)
            else -> android.graphics.Typeface.DEFAULT_BOLD
        }
        titleView.typeface = typeface

        val textColorStr = prefs.getString("folder_text_color", "#1D1D1F") ?: "#1D1D1F"
        try {
            titleView.setTextColor(android.graphics.Color.parseColor(textColorStr))
        } catch (e: Exception) {
            titleView.setTextColor(android.graphics.Color.parseColor("#1D1D1F"))
        }

        // Parse internal grid rows/cols
        val gridStr = prefs.getString("folder_grid", "3x3") ?: "3x3"
        val parts = gridStr.split("x")
        val gridCols = parts.getOrNull(1)?.toIntOrNull() ?: 3
        rvApps.layoutManager = GridLayoutManager(this, gridCols)

        // Bind folder apps inside popup
        val folderApps = getFolderAppsMock(folder.packageName.toString())
        val folderAdapter = AppDrawerAdapter(folderApps, isWhiteText = false) { app ->
            launchApp(app)
            dialog.dismiss()
            hideAppDrawer()
        }
        rvApps.adapter = folderAdapter

        // Window sizes
        val displayMetrics = resources.displayMetrics
        val width = (displayMetrics.widthPixels * (prefs.getInt("folder_window_width", 85) / 100f)).toInt()
        val height = (displayMetrics.heightPixels * (prefs.getInt("folder_window_height", 60) / 100f)).toInt()

        dialog.show()
        dialog.window?.setLayout(width, height)

        // Animations
        val animStyle = prefs.getString("folder_anim_style", "scale")
        when (animStyle) {
            "fade" -> {
                card.alpha = 0f
                card.animate().alpha(1f).setDuration(250).start()
            }
            "scale" -> {
                card.scaleX = 0.5f
                card.scaleY = 0.5f
                card.animate().scaleX(1.0f).scaleY(1.0f).setDuration(250).start()
            }
            "slide" -> {
                card.translationY = 300f
                card.animate().translationY(0f).setDuration(250).start()
            }
        }
    }

    private fun getFolderAppsMock(folderPkg: String): List<AppInfo> {
        return if (folderPkg.contains("google")) {
            allApps.filter { it.packageName.toString().contains("google", ignoreCase = true) || it.packageName.toString().contains("chrome", ignoreCase = true) || it.packageName.toString().contains("android", ignoreCase = true) }
                .ifEmpty { allApps.take(6) }
        } else {
            allApps.takeLast(6)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestures() {
        // Scale/Pinch Detector
        val scaleDetector = android.view.ScaleGestureDetector(this, object : android.view.ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: android.view.ScaleGestureDetector): Boolean {
                if (detector.scaleFactor < 0.82f) { // Pinch out (Zoom out)
                    showSplitSelectionMenu()
                    return true
                }
                return false
            }
        })

        // Swipe & Tap & LongPress Detector
        val gestureDetector = android.view.GestureDetector(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: android.view.MotionEvent) {
                showSplitSelectionMenu()
            }

            override fun onDoubleTap(e: android.view.MotionEvent): Boolean {
                vibratePhone()
                return true
            }

            override fun onSingleTapConfirmed(e: android.view.MotionEvent): Boolean {
                return true
            }

            override fun onFling(
                e1: android.view.MotionEvent?,
                e2: android.view.MotionEvent,
                vx: Float,
                vy: Float
            ): Boolean {
                if (e1 != null) {
                    val diffY = e2.y - e1.y
                    val diffX = e2.x - e1.x
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        return false
                    } else {
                        if (Math.abs(diffY) > 100 && Math.abs(vy) > 100) {
                            if (diffY > 0) {
                                expandNotificationPanel()
                            } else {
                                showAppDrawer()
                            }
                            return true
                        }
                    }
                }
                return false
            }
        })

        val touchListener = android.view.View.OnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            gestureDetector.onTouchEvent(event)
            false
        }

        binding.mainRoot.setOnTouchListener(touchListener)
        binding.workspaceGrid.setOnTouchListener(touchListener)

        // Setup Split Menu interactions
        binding.splitSelectionMenu.setOnClickListener {
            binding.splitSelectionMenu.visibility = android.view.View.GONE
        }

        binding.btnSplitSettings.setOnClickListener {
            binding.splitSelectionMenu.visibility = android.view.View.GONE
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }

        binding.btnSplitWidgets.setOnClickListener {
            binding.splitSelectionMenu.visibility = android.view.View.GONE
            showWidgetPicker()
        }
    }

    private fun showSplitSelectionMenu() {
        binding.splitSelectionMenu.visibility = android.view.View.VISIBLE
        binding.splitSelectionMenu.alpha = 0f
        binding.splitSelectionMenu.animate().alpha(1f).setDuration(250).start()
    }

    private fun showWidgetPicker() {
        val options = arrayOf(
            "🕒 Uhrzeit-Widget",
            "🌤️ Wetter-Widget",
            "🔋 Akkustand-Widget",
            "📅 Kalender-Widget"
        )
        android.app.AlertDialog.Builder(this)
            .setTitle("Widget auswählen")
            .setItems(options) { _, which ->
                val type = when (which) {
                    0 -> "uhrzeit"
                    1 -> "wetter"
                    2 -> "akkustand"
                    else -> "kalender"
                }
                addWidgetToScreen(type)
            }
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addWidgetToScreen(type: String) {
        val density = resources.displayMetrics.density
        val card = com.google.android.material.card.MaterialCardView(this).apply {
            cardElevation = 6 * density
            radius = 16 * density
            setCardBackgroundColor(android.graphics.Color.parseColor("#E62C2C2E")) // Translucent dark card
            strokeWidth = 0
        }

        // Layout parameters
        val lp = android.widget.FrameLayout.LayoutParams(
            (160 * density).toInt(),
            (100 * density).toInt()
        ).apply {
            leftMargin = (30 * density).toInt()
            topMargin = (100 * density).toInt()
        }
        card.layoutParams = lp

        // Dynamic Widget content
        val contentLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding((12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt(), (12 * density).toInt())
        }

        val emojiView = android.widget.TextView(this).apply {
            textSize = 22f
            gravity = android.view.Gravity.CENTER
        }

        val titleView = android.widget.TextView(this).apply {
            textSize = 14f
            setTextColor(android.graphics.Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = android.view.Gravity.CENTER
        }

        val subtitleView = android.widget.TextView(this).apply {
            textSize = 11f
            setTextColor(android.graphics.Color.parseColor("#B0FFFFFF"))
            gravity = android.view.Gravity.CENTER
            setSingleLine(true)
        }

        // Apply content based on type
        when (type) {
            "uhrzeit" -> {
                emojiView.text = "🕒"
                titleView.text = "14:30"
                subtitleView.text = "Digitaluhr"
            }
            "wetter" -> {
                emojiView.text = "🌤️"
                titleView.text = "24°C"
                subtitleView.text = "FHD+ Sonnig"
            }
            "akkustand" -> {
                emojiView.text = "🔋"
                titleView.text = "85%"
                subtitleView.text = "Ladevorgang..."
            }
            "kalender" -> {
                emojiView.text = "📅"
                titleView.text = "30. Juli"
                subtitleView.text = "Dienstag"
            }
        }

        contentLayout.addView(emojiView)
        contentLayout.addView(titleView)
        contentLayout.addView(subtitleView)
        card.addView(contentLayout)

        // Draggable touch mechanism with Snap-to-Grid and Overlap checks
        card.setOnTouchListener(object : android.view.View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f

            override fun onTouch(v: android.view.View, event: android.view.MotionEvent): Boolean {
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val snapToGrid = prefs.getBoolean("widget_snap_to_grid", true)
                val allowOverlap = prefs.getBoolean("widget_overlap", false)

                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        var newX = event.rawX + dX
                        var newY = event.rawY + dY

                        if (snapToGrid) {
                            val cellSize = 80 * density
                            newX = (Math.round(newX / cellSize) * cellSize)
                            newY = (Math.round(newY / cellSize) * cellSize)
                        }

                        // Constraints inside the widget container area
                        val maxX = (binding.widgetContainer.width - v.width).toFloat()
                        val maxY = (binding.widgetContainer.height - v.height).toFloat()
                        newX = Math.max(0f, Math.min(newX, maxX))
                        newY = Math.max(0f, Math.min(newY, maxY))

                        // Collision detection (if Overlap is disabled)
                        if (!allowOverlap) {
                            var overlaps = false
                            for (i in 0 until binding.widgetContainer.childCount) {
                                val sibling = binding.widgetContainer.getChildAt(i)
                                if (sibling != v) {
                                    val r1 = android.graphics.Rect(newX.toInt(), newY.toInt(), (newX + v.width).toInt(), (newY + v.height).toInt())
                                    val r2 = android.graphics.Rect(sibling.x.toInt(), sibling.y.toInt(), (sibling.x + sibling.width).toInt(), (sibling.y + sibling.height).toInt())
                                    if (android.graphics.Rect.intersects(r1, r2)) {
                                        overlaps = true
                                        break
                                    }
                                }
                            }
                            if (!overlaps) {
                                v.x = newX
                                v.y = newY
                            }
                        } else {
                            v.x = newX
                            v.y = newY
                        }
                    }
                }
                return true
            }
        })

        // Delete widget on long click
        card.setOnLongClickListener {
            binding.widgetContainer.removeView(card)
            android.widget.Toast.makeText(this, "Widget entfernt", android.widget.Toast.LENGTH_SHORT).show()
            true
        }

        binding.widgetContainer.addView(card)
        android.widget.Toast.makeText(this, "Widget hinzugefügt! Zum Verschieben ziehen, gedrückt halten zum Löschen.", android.widget.Toast.LENGTH_LONG).show()
    }

    private fun showAppDrawer() {
        binding.appDrawerContainer.visibility = View.VISIBLE
        binding.appDrawerContainer.alpha = 0f
        binding.appDrawerContainer.animate().alpha(1f).setDuration(250).start()
        binding.searchInput.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchInput, InputMethodManager.SHOW_IMPLICIT)

        // Dock auto-hide check
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean("dock_auto_hide", false)) {
            binding.bottomDock.visibility = View.GONE
        }
    }

    private fun hideAppDrawer() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
        binding.appDrawerContainer.animate().alpha(0f).setDuration(200).withEndAction {
            binding.appDrawerContainer.visibility = View.GONE
            binding.searchInput.setText("")
        }.start()

        // Dock auto-hide check (restore visibility)
        binding.bottomDock.visibility = View.VISIBLE
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

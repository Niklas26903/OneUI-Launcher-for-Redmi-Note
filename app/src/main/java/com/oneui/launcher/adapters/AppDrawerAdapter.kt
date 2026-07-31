package com.oneui.launcher.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oneui.launcher.R
import com.oneui.launcher.models.AppInfo

class AppDrawerAdapter(
    private var appsList: List<AppInfo>,
    private val isWhiteText: Boolean = false,
    private val onItemClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppDrawerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.app_icon)
        val label: TextView = view.findViewById(R.id.app_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val app = appsList[position]

        // Load custom Preferences
        val prefs = context.getSharedPreferences("OneUI_Prefs", Context.MODE_PRIVATE)

        // 1. Icon resizing
        val iconSizePct = prefs.getInt("icon_size", 100) // Default 100%
        val density = context.resources.displayMetrics.density
        val baseSizePx = (60 * density).toInt()
        val scaledSizePx = (baseSizePx * (iconSizePct / 100f)).toInt()

        holder.icon.layoutParams.width = scaledSizePx
        holder.icon.layoutParams.height = scaledSizePx

        // 2. Closed Folder custom rendering
        if (app.packageName.toString().startsWith("folder:")) {
            val folderSize = prefs.getInt("folder_size", 64)
            val folderBgColorStr = prefs.getString("folder_bg_color", "#FFFFFF") ?: "#FFFFFF"
            val folderTransparency = prefs.getInt("folder_transparency", 30)
            val folderShape = prefs.getString("folder_shape", "squircle") ?: "squircle"
            val folderBorderColorStr = prefs.getString("folder_border_color", "#E0E0E0") ?: "#E0E0E0"
            val folderBorderWidth = prefs.getInt("folder_border_width", 2)

            // Parse Colors
            val bgColor = try { Color.parseColor(folderBgColorStr) } catch(e: Exception) { Color.WHITE }
            val borderColor = try { Color.parseColor(folderBorderColorStr) } catch(e: Exception) { Color.LTGRAY }

            // Build Programmatic Shape Drawable
            val bgDrawable = GradientDrawable()
            when (folderShape) {
                "round" -> {
                    bgDrawable.shape = GradientDrawable.OVAL
                }
                "square" -> {
                    bgDrawable.shape = GradientDrawable.RECTANGLE
                    bgDrawable.cornerRadius = 0f
                }
                "squircle" -> {
                    bgDrawable.shape = GradientDrawable.RECTANGLE
                    bgDrawable.cornerRadius = (folderSize / 3f) * density
                }
                "custom" -> {
                    bgDrawable.shape = GradientDrawable.RECTANGLE
                    bgDrawable.cornerRadius = 24f * density
                }
            }

            // Apply Transparency (alpha opacity)
            val alpha = ((100 - folderTransparency) * 2.55).toInt().coerceIn(0, 255)
            bgDrawable.setColor(Color.argb(alpha, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor)))

            // Set border
            if (folderBorderWidth > 0) {
                bgDrawable.setStroke((folderBorderWidth * density).toInt(), borderColor)
            }

            holder.icon.background = bgDrawable
            val fSizePx = (folderSize * density).toInt()
            holder.icon.layoutParams.width = fSizePx
            holder.icon.layoutParams.height = fSizePx
            holder.icon.setPadding((10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt(), (10 * density).toInt())

            // Generate mini-grid preview
            val miniGridStr = prefs.getString("folder_mini_grid", "3x3") ?: "3x3"
            val parts = miniGridStr.split("x")
            val rows = parts.getOrNull(0)?.toIntOrNull() ?: 3
            val cols = parts.getOrNull(1)?.toIntOrNull() ?: 3

            // Create mini preview bitmap
            val previewSize = 120
            val bitmap = Bitmap.createBitmap(previewSize, previewSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val cellW = previewSize / cols
            val cellH = previewSize / rows
            val iconPadding = 4

            // Get apps inside folder (mock subset)
            val folderApps = getFolderAppsMock(context, app.packageName.toString())
            folderApps.take(rows * cols).forEachIndexed { idx, item ->
                val r = idx / cols
                val c = idx % cols
                val left = c * cellW + iconPadding
                val top = r * cellH + iconPadding
                val right = (c + 1) * cellW - iconPadding
                val bottom = (r + 1) * cellH - iconPadding
                item.icon?.let { drawable ->
                    drawable.setBounds(left, top, right, bottom)
                    drawable.draw(canvas)
                }
            }
            holder.icon.setImageDrawable(BitmapDrawable(context.resources, bitmap))

        } else {
            // Regular app icon setup
            holder.icon.background = null
            holder.icon.setPadding(0, 0, 0, 0)

            // Apply custom tinting / icon pack if customized
            val iconPackValue = prefs.getString("icon_pack_package", "default") ?: "default"
            if (iconPackValue != "default" && iconPackValue.startsWith("#")) {
                try {
                    val tintColor = Color.parseColor(iconPackValue)
                    holder.icon.setImageDrawable(app.icon)
                    holder.icon.setColorFilter(tintColor, android.graphics.PorterDuff.Mode.SRC_ATOP)
                } catch (e: Exception) {
                    holder.icon.clearColorFilter()
                    holder.icon.setImageDrawable(app.icon)
                }
            } else {
                holder.icon.clearColorFilter()
                holder.icon.setImageDrawable(app.icon)
            }
        }

        // 3. App Text label custom styling
        val showLabels = prefs.getBoolean("show_icon_labels", true)
        if (showLabels) {
            holder.label.visibility = View.VISIBLE
            holder.label.text = app.label

            // Set max lines (1 vs 2 lines)
            val maxLines = prefs.getInt("icon_label_max_lines", 1)
            holder.label.maxLines = maxLines

            // Set font size
            val labelSize = prefs.getInt("icon_label_size", 12)
            holder.label.textSize = labelSize.toFloat()

            // Set label color
            if (isWhiteText) {
                holder.label.setTextColor(Color.WHITE)
            } else {
                val labelColorStr = prefs.getString("icon_label_color", "#1D1D1F") ?: "#1D1D1F"
                try {
                    holder.label.setTextColor(Color.parseColor(labelColorStr))
                } catch (e: Exception) {
                    holder.label.setTextColor(Color.parseColor("#1D1D1F"))
                }
            }
        } else {
            holder.label.visibility = View.GONE
        }

        holder.icon.requestLayout()

        holder.itemView.setOnClickListener {
            onItemClick(app)
        }
    }

    override fun getItemCount(): Int = appsList.size

    fun updateData(newApps: List<AppInfo>) {
        appsList = newApps
        notifyDataSetChanged()
    }

    // Static helper for mock apps in folder
    private fun getFolderAppsMock(context: Context, folderPkg: String): List<AppInfo> {
        // Resolve installed launchable apps
        val pm = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null).apply {
            addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(intent, 0)
        val allApps = resolveInfos.map { info ->
            AppInfo(
                label = info.loadLabel(pm),
                packageName = info.activityInfo.packageName,
                icon = info.loadIcon(pm),
                componentName = info.activityInfo.name
            )
        }.sortedBy { it.label.toString().lowercase() }

        return if (folderPkg.contains("google")) {
            allApps.filter { it.packageName.toString().contains("google", ignoreCase = true) || it.packageName.toString().contains("chrome", ignoreCase = true) || it.packageName.toString().contains("android", ignoreCase = true) }
                .ifEmpty { allApps.take(6) }
        } else {
            allApps.takeLast(6)
        }
    }
}

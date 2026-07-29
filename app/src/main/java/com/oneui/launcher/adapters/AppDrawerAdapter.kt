package com.oneui.launcher.adapters

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
        val app = appsList[position]
        holder.icon.setImageDrawable(app.icon)
        holder.label.text = app.label
        if (isWhiteText) {
            holder.label.setTextColor(android.graphics.Color.WHITE)
        } else {
            holder.label.setTextColor(android.graphics.Color.parseColor("#1D1D1F"))
        }
        holder.itemView.setOnClickListener {
            onItemClick(app)
        }
    }

    override fun getItemCount(): Int = appsList.size

    fun updateData(newApps: List<AppInfo>) {
        appsList = newApps
        notifyDataSetChanged()
    }
}

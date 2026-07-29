package com.oneui.launcher.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oneui.launcher.R
import com.oneui.launcher.routines.Routine

class RoutinesAdapter(
    private var list: List<Routine>,
    private val onDelete: (Routine) -> Unit
) : RecyclerView.Adapter<RoutinesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_routine_name)
        val desc: TextView = view.findViewById(R.id.tv_routine_desc)
        val deleteBtn: Button = view.findViewById(R.id.btn_delete_routine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_routine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val r = list[position]
        holder.name.text = r.name
        holder.desc.text = "WENN [${r.triggerType.name}] ist '${r.triggerValue}' DANN [${r.actionType.name}] = '${r.actionValue}'"
        holder.deleteBtn.setOnClickListener {
            onDelete(r)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Routine>) {
        list = newList
        notifyDataSetChanged()
    }
}

package com.oneui.launcher.routines

import android.content.Context
import com.oneui.launcher.routines.Routine
import com.oneui.launcher.routines.TriggerType
import com.oneui.launcher.routines.ActionType
import org.json.JSONArray
import org.json.JSONObject

class RoutinesStorage(context: Context) {
    private val prefs = context.getSharedPreferences("OneUI_Routines", Context.MODE_PRIVATE)

    fun getRoutines(): List<Routine> {
        val jsonStr = prefs.getString("routines_list", null) ?: return emptyList()
        val list = mutableListOf<Routine>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    Routine(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        triggerType = TriggerType.valueOf(obj.getString("triggerType")),
                        triggerValue = obj.getString("triggerValue"),
                        actionType = ActionType.valueOf(obj.getString("actionType")),
                        actionValue = obj.getString("actionValue"),
                        isEnabled = obj.optBoolean("isEnabled", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun saveRoutines(routines: List<Routine>) {
        val arr = JSONArray()
        for (r in routines) {
            val obj = JSONObject().apply {
                put("id", r.id)
                put("name", r.name)
                put("triggerType", r.triggerType.name)
                put("triggerValue", r.triggerValue)
                put("actionType", r.actionType.name)
                put("actionValue", r.actionValue)
                put("isEnabled", r.isEnabled)
            }
            arr.put(obj)
        }
        prefs.edit().putString("routines_list", arr.toString()).apply()
    }

    fun addRoutine(routine: Routine) {
        val current = getRoutines().toMutableList()
        current.add(routine)
        saveRoutines(current)
    }

    fun deleteRoutine(id: String) {
        val current = getRoutines().filter { it.id != id }
        saveRoutines(current)
    }
}

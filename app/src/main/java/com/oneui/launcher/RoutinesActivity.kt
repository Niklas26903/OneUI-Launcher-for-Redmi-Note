package com.oneui.launcher

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.oneui.launcher.adapters.RoutinesAdapter
import com.oneui.launcher.databinding.ActivityRoutinesBinding
import com.oneui.launcher.routines.ActionType
import com.oneui.launcher.routines.Routine
import com.oneui.launcher.routines.RoutinesStorage
import com.oneui.launcher.routines.TriggerType
import java.util.UUID

class RoutinesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutinesBinding
    private lateinit var storage: RoutinesStorage
    private lateinit var adapter: RoutinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = RoutinesStorage(this)
        setupSpinners()
        setupList()
        setupListeners()
    }

    private fun setupSpinners() {
        val triggerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            TriggerType.values().map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spTriggerType.adapter = triggerAdapter

        val actionAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            ActionType.values().map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.spActionType.adapter = actionAdapter
    }

    private fun setupList() {
        adapter = RoutinesAdapter(storage.getRoutines()) { routine ->
            storage.deleteRoutine(routine.id)
            adapter.updateData(storage.getRoutines())
            Toast.makeText(this, "Routine gelöscht", Toast.LENGTH_SHORT).show()
        }
        binding.rvRoutines.layoutManager = LinearLayoutManager(this)
        binding.rvRoutines.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnSaveRoutine.setOnClickListener {
            val name = binding.etRoutineName.text.toString().trim()
            val triggerVal = binding.etTriggerValue.text.toString().trim()
            val actionVal = binding.etActionValue.text.toString().trim()

            if (name.isEmpty() || triggerVal.isEmpty() || actionVal.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val triggerType = TriggerType.valueOf(binding.spTriggerType.selectedItem.toString())
            val actionType = ActionType.valueOf(binding.spActionType.selectedItem.toString())

            val newRoutine = Routine(
                id = UUID.randomUUID().toString(),
                name = name,
                triggerType = triggerType,
                triggerValue = triggerVal,
                actionType = actionType,
                actionValue = actionVal,
                isEnabled = true
            )

            storage.addRoutine(newRoutine)
            adapter.updateData(storage.getRoutines())

            // Clear inputs
            binding.etRoutineName.setText("")
            binding.etTriggerValue.setText("")
            binding.etActionValue.setText("")

            Toast.makeText(this, "Routine erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show()
        }
    }
}

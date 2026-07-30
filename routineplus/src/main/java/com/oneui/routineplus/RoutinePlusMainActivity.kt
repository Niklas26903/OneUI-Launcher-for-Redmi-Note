package com.oneui.routineplus

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneui.routineplus.databinding.ActivityRoutinePlusMainBinding

data class MacroStep(val x: Int, val y: Int, val delayMs: Long)

class RoutinePlusMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoutinePlusMainBinding
    private val macroSteps = mutableListOf<MacroStep>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoutinePlusMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMacroControls()
        setupFingerprintSimulation()
        setupButtonShortcuts()
    }

    private fun setupMacroControls() {
        binding.btnAddMacroStep.setOnClickListener {
            val xStr = binding.etMacroX.text.toString()
            val yStr = binding.etMacroY.text.toString()
            val delayStr = binding.etMacroDelay.text.toString()

            if (xStr.isEmpty() || yStr.isEmpty()) {
                Toast.makeText(this, "Bitte Koordinaten X und Y eingeben!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val x = xStr.toInt()
            val y = yStr.toInt()
            val delay = if (delayStr.isEmpty()) 500L else delayStr.toLong()

            macroSteps.add(MacroStep(x, y, delay))
            updateMacroStepsText()

            binding.etMacroX.setText("")
            binding.etMacroY.setText("")
            binding.etMacroDelay.setText("")
            Toast.makeText(this, "Schritt hinzugefügt!", Toast.LENGTH_SHORT).show()
        }

        binding.btnPlayMacro.setOnClickListener {
            if (macroSteps.isEmpty()) {
                Toast.makeText(this, "Keine Makroschritte definiert!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Makro startet...", Toast.LENGTH_SHORT).show()
            executeMacroStep(0)
        }

        binding.btnClearMacro.setOnClickListener {
            macroSteps.clear()
            updateMacroStepsText()
            Toast.makeText(this, "Makro zurückgesetzt", Toast.LENGTH_SHORT).show()
        }
    }

    private fun executeMacroStep(index: Int) {
        if (index >= macroSteps.size) {
            Toast.makeText(this, "✅ Makro-Ausführung abgeschlossen!", Toast.LENGTH_LONG).show()
            return
        }

        val step = macroSteps[index]
        Toast.makeText(this, "Simuliere Tippen bei (${step.x}, ${step.y})", Toast.LENGTH_SHORT).show()

        handler.postDelayed({
            executeMacroStep(index + 1)
        }, step.delayMs)
    }

    private fun updateMacroStepsText() {
        if (macroSteps.isEmpty()) {
            binding.tvMacroSteps.text = "Schritte: Keine"
            return
        }
        val sb = StringBuilder("Schritte:\n")
        macroSteps.forEachIndexed { i, step ->
            sb.append("${i + 1}. Klick bei (${step.x}, ${step.y}) mit Verzögerung von ${step.delayMs}ms\n")
        }
        binding.tvMacroSteps.text = sb.toString().trim()
    }

    private fun setupFingerprintSimulation() {
        val actions = listOf(
            "Gerät stummschalten (Silent)",
            "Musik-App (Spotify) starten",
            "Taschenlampe einschalten",
            "Sparsamer Akkustand-Modus aktivieren"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spFingerprintAction.adapter = adapter

        binding.btnSimulateFingerprint.setOnClickListener {
            val selectedAction = binding.spFingerprintAction.selectedItem.toString()
            Toast.makeText(this, "👆 Fingerprint erkannt! Führe Aktion aus:\n$selectedAction", Toast.LENGTH_LONG).show()
            // Emulate execution logic
            when (binding.spFingerprintAction.selectedItemPosition) {
                0 -> {
                    val am = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                    try {
                        am.ringerMode = android.media.AudioManager.RINGER_MODE_SILENT
                        Toast.makeText(this, "Lautlos-Modus aktiviert!", Toast.LENGTH_SHORT).show()
                    } catch (e: SecurityException) {
                        Toast.makeText(this, "Simuliert: Silent-Modus (DND-Zugriff erforderlich)", Toast.LENGTH_LONG).show()
                    }
                }
                1 -> {
                    Toast.makeText(this, "Simuliere App-Start: Spotify", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    Toast.makeText(this, "Taschenlampe AN", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    Toast.makeText(this, "Energiesparmodus AN", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupButtonShortcuts() {
        val actions = listOf(
            "Kamera-App starten",
            "Letzte Apps anzeigen",
            "Screenshot aufnehmen",
            "Dark Mode umschalten"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spButtonAction.adapter = adapter

        val runShortcut = { trigger: String ->
            val selectedAction = binding.spButtonAction.selectedItem.toString()
            Toast.makeText(this, "🎛️ Shortcut ($trigger) ausgelöst!\nFühre aus: $selectedAction", Toast.LENGTH_LONG).show()
        }

        binding.btnShortcutVolUp.setOnClickListener {
            runShortcut("Lautstärke +")
        }

        binding.btnShortcutVolDown.setOnClickListener {
            runShortcut("Lautstärke -")
        }
    }
}

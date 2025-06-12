package com.oni_dev.propinasapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TipHistoryAdapter
    private lateinit var buttonClearAll: MaterialButton
    private lateinit var tipHistoryManager: TipHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        tipHistoryManager = TipHistoryManager.getInstance(this)
        recyclerView = findViewById(R.id.recyclerViewTips)
        buttonClearAll = findViewById(R.id.buttonClearAll)
        adapter = TipHistoryAdapter { record ->
            lifecycleScope.launch {
                tipHistoryManager.removeRecord(record)
                loadHistory()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        buttonClearAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("¿Borrar todo el historial?")
                .setMessage("Esta acción no se puede deshacer.")
                .setPositiveButton("Sí") { _, _ ->
                    lifecycleScope.launch {
                        tipHistoryManager.clearHistory()
                        loadHistory()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }

        loadHistory()
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            val history: List<TipRecord> = tipHistoryManager.getHistory()
            adapter.submitList(history)
            if (history.isEmpty()) {
                Toast.makeText(this@HistoryActivity, "No hay propinas registradas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

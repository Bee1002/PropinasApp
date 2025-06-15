package com.oni_dev.propinasapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {
    private lateinit var textViewTotalAmount: TextView
    private lateinit var textViewAmountPerEmployee: TextView
    private lateinit var editTextEmployeeCount: EditText
    private lateinit var buttonConfirm: Button
    private lateinit var buttonBack: Button
    private lateinit var layoutEmployeeNames: LinearLayout
    private var totalAmount: Double = 0.0
    private val employeeNames = mutableListOf<String>()
    private lateinit var tipHistoryManager: TipHistoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        tipHistoryManager = TipHistoryManager.getInstance(this)

        initializeViews()
        setupTotalAmount()
        setupButtons()
        initializeEmployeeNames()

        val amount = intent.getDoubleExtra("amount", 0.0)
        val tipAmount = intent.getDoubleExtra("tipAmount", 0.0)

        val textViewAmount = findViewById<TextView>(R.id.textViewAmount)
        //val textViewTipAmount = findViewById<TextView>(R.id.textViewTipAmount)
        val textViewTotalAmount = findViewById<TextView>(R.id.textViewTotalAmount)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        textViewAmount.text = "Monto: $amount"
        //textViewTipAmount.text = "Propina: $tipAmount"
        textViewTotalAmount.text = "Total: ${amount + tipAmount}"

        buttonSave.setOnClickListener {
            lifecycleScope.launch {
                val record = TipRecord(
                    date = Date(),
                    totalAmount = amount + tipAmount,
                    employeeCount = 1,  // Por defecto, puedes ajustar según necesites
                    amountPerEmployee = (amount + tipAmount) / 1,
                    employeeNames = listOf("Empleado 1") // Por defecto, puedes ajustar según necesites
                )
                val success = tipHistoryManager.addRecord(record)
                if (success) {
                    Toast.makeText(this@ResultActivity, "Propina guardada correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@ResultActivity, "Error al guardar la propina", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeViews() {
        try {
            textViewTotalAmount = findViewById(R.id.textViewTotalAmount)
            textViewAmountPerEmployee = findViewById(R.id.textViewAmountPerEmployee)
            editTextEmployeeCount = findViewById(R.id.editTextEmployeeCount)
            buttonConfirm = findViewById(R.id.buttonConfirm)
            buttonBack = findViewById(R.id.buttonBack)
            layoutEmployeeNames = findViewById(R.id.layoutEmployeeNames)
        } catch (e: Exception) {
            Log.e("ResultActivity", "Error al inicializar vistas", e)
            Toast.makeText(this, "Error al inicializar vistas: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupTotalAmount() {
        try {
            totalAmount = intent.getDoubleExtra("amount", 0.0)
            if (totalAmount <= 0) {
                Log.e("ResultActivity", "Monto inválido: $totalAmount")
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
            textViewTotalAmount.text = currencyFormat.format(totalAmount)
        } catch (e: Exception) {
            Log.e("ResultActivity", "Error al configurar monto total", e)
            Toast.makeText(this, "Error al configurar monto: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupButtons() {
        try {
            buttonConfirm.setOnClickListener {
                calculateAndSaveTip()
            }

            buttonBack.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            Log.e("ResultActivity", "Error al configurar botones", e)
            Toast.makeText(this, "Error al configurar botones: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun initializeEmployeeNames() {
        employeeNames.clear()
        val defaultNames = listOf(
            "Alejandra", "Laura", "Sofia", "Yolanda"
        )
        employeeNames.addAll(defaultNames)
    }

    private fun updateEmployeeNamesList(employeeCount: Int) {
        layoutEmployeeNames.removeAllViews()

        for (i in 0 until employeeCount) {
            val nameView = TextView(this).apply {
                text = "${i + 1}. ${employeeNames[i]}"
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.black, theme))
                setPadding(0, 8, 0, 8)
            }
            layoutEmployeeNames.addView(nameView)
        }
    }

    private fun calculateAndSaveTip() {
        try {
            val employeeCountText = editTextEmployeeCount.text?.toString()?.trim() ?: ""
            if (employeeCountText.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese el número de empleados", Toast.LENGTH_SHORT).show()
                return
            }

            val employeeCount = employeeCountText.toIntOrNull()
            if (employeeCount == null || employeeCount <= 0) {
                Toast.makeText(this, "Por favor ingrese un número válido de empleados", Toast.LENGTH_SHORT).show()
                return
            }

            val amountPerEmployee = totalAmount / employeeCount
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
            textViewAmountPerEmployee.text = "Por empleado: ${currencyFormat.format(amountPerEmployee)}"

            // Actualizar la lista de nombres de empleados
            updateEmployeeNamesList(employeeCount)

            lifecycleScope.launch {
                val record = TipRecord(
                    totalAmount = totalAmount,
                    employeeCount = employeeCount,
                    amountPerEmployee = amountPerEmployee,
                    employeeNames = employeeNames.take(employeeCount),
                    date = Date()
                )
                val success = tipHistoryManager.addRecord(record)
                if (success) {
                    Toast.makeText(this@ResultActivity, "Propina guardada en el historial", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ResultActivity, "Error al guardar la propina", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("ResultActivity", "Error al calcular y guardar propina", e)
            Toast.makeText(this, "Error al calcular la propina: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            editTextEmployeeCount.text?.clear()
            textViewAmountPerEmployee.text = ""
            layoutEmployeeNames.removeAllViews()
        } catch (e: Exception) {
            Log.e("ResultActivity", "Error en onResume", e)
        }
    }
}

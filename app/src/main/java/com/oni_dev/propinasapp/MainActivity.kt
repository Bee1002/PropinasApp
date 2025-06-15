package com.oni_dev.propinasapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import android.text.Editable
import android.text.TextWatcher

class MainActivity : AppCompatActivity() {
    private lateinit var amountInput: TextInputEditText
    private lateinit var amountLayout: TextInputLayout
    private lateinit var calculateButton: MaterialButton
    private lateinit var historyButton: MaterialButton
    private lateinit var tipHistoryManager: TipHistoryManager
    private lateinit var textViewEnteredAmount: TextView
    //private lateinit var textViewRemainingBudget: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tipHistoryManager = TipHistoryManager.getInstance(this)
        setupViews()
        setupClickListeners()
        updateBudgetDisplay()
        }

    private fun setupViews() {
        amountInput = findViewById(R.id.amountInput)
        amountLayout = findViewById(R.id.amountLayout)
        calculateButton = findViewById(R.id.calculateButton)
        historyButton = findViewById(R.id.historyButton)
        textViewEnteredAmount = findViewById(R.id.textViewEnteredAmount)
        //textViewRemainingBudget = findViewById(R.id.textViewRemainingBudget)
    }

    private fun setupClickListeners() {
        calculateButton.setOnClickListener {
            try {
                val amountText = amountInput.text.toString()
                if (amountText.isNotEmpty()) {
                    val amount = amountText.toDouble()
                    lifecycleScope.launch {
                        val tipAmount = tipHistoryManager.calculateTipAmount(amount)
                        val remainingBudget = tipHistoryManager.getRemainingBudget()

                        if (tipAmount <= remainingBudget) {
                            val intent = Intent(this@MainActivity, ResultActivity::class.java).apply {
                                putExtra("amount", amount)
                                putExtra("tipAmount", tipAmount)
                            }
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "No hay suficiente presupuesto disponible. Presupuesto restante: ${formatCurrency(remainingBudget)}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    amountLayout.error = "Por favor ingrese un monto"
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        historyButton.setOnClickListener {
            try {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al abrir el historial: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        amountInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val amountText = s?.toString() ?: ""
                val amount = amountText.toDoubleOrNull() ?: 0.0
                textViewEnteredAmount.text = "Monto ingresado: %.2f €".format(amount)
            }
        })
    }

   private fun updateBudgetDisplay() {
        lifecycleScope.launch {
            val remainingBudget = tipHistoryManager.getRemainingBudget()
            //textViewRemainingBudget.text = "Presupuesto restante: %.2f €".format(remainingBudget)
        }
    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("es", "ES")).format(amount)
    }

    override fun onResume() {
        super.onResume()
        updateBudgetDisplay()

    }
}
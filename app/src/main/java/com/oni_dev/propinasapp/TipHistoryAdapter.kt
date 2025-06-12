package com.oni_dev.propinasapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TipHistoryAdapter(
    private val onDeleteClick: (TipRecord) -> Unit
) : ListAdapter<TipRecord, TipHistoryAdapter.ViewHolder>(DiffCallback()) {

    private var totalAmount: Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = getItem(position)
        holder.bind(record)
    }

    override fun submitList(list: List<TipRecord>?) {
        totalAmount = if (list != null) list.sumOf { it.totalAmount } else 0.0
        super.submitList(list)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val textViewTotalAmount: TextView = itemView.findViewById(R.id.textViewTotalAmount)
        private val textViewEmployeeCount: TextView = itemView.findViewById(R.id.textViewEmployeeCount)
        private val textViewAmountPerEmployee: TextView = itemView.findViewById(R.id.textViewAmountPerEmployee)
        private val textViewTotalAccumulated: TextView = itemView.findViewById(R.id.textViewTotalAccumulated)
        private val layoutEmployeeNames: LinearLayout = itemView.findViewById(R.id.layoutEmployeeNames)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(record: TipRecord) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "ES"))

            textViewDate.text = dateFormat.format(record.date)
            textViewTotalAmount.text = "💰 Monto Total: ${currencyFormat.format(record.totalAmount)}"
            textViewEmployeeCount.text = "👥 Total de Empleados: ${record.employeeCount}"
            textViewAmountPerEmployee.text = "💵 Por empleado: ${currencyFormat.format(record.amountPerEmployee)}"
            textViewTotalAccumulated.text = "🏦 Total Acumulado: ${currencyFormat.format(totalAmount)}"

            // Mostrar nombres de empleados
            layoutEmployeeNames.removeAllViews()
            record.employeeNames.forEachIndexed { index, name ->
                val nameView = TextView(itemView.context).apply {
                    text = "${index + 1}. $name"
                    textSize = 14f
                    setTextColor(itemView.context.resources.getColor(android.R.color.black, null))
                    setPadding(0, 4, 0, 4)
                }
                layoutEmployeeNames.addView(nameView)
            }

            buttonDelete.setOnClickListener {
                onDeleteClick(record)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TipRecord>() {
        override fun areItemsTheSame(oldItem: TipRecord, newItem: TipRecord): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: TipRecord, newItem: TipRecord): Boolean {
            return oldItem == newItem
        }
    }
} 
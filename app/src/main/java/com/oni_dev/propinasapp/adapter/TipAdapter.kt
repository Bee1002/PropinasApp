package com.oni_dev.propinasapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oni_dev.propinasapp.R
import com.example.propinasapp.model.Tip
import java.text.SimpleDateFormat
import java.util.*

class TipAdapter(private var tips: List<Tip>) : RecyclerView.Adapter<TipAdapter.TipViewHolder>() {

    class TipViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountText: TextView = view.findViewById(R.id.textViewAmount)
        val dateText: TextView = view.findViewById(R.id.textViewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = tips[position]
        holder.amountText.text = String.format("%.2f €", tip.amount)
        holder.dateText.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(Date(tip.date))
    }

    override fun getItemCount() = tips.size

    fun updateTips(newTips: List<Tip>) {
        tips = newTips
        notifyDataSetChanged()
    }
} 
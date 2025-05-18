package com.example.culturewaveinter.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.ReserveWithEvent
import com.example.culturewaveinter.R

class ReservesAdapter(
    private val reserves: MutableList<ReserveWithEvent>,
    private val onCancelClick: (Int) -> Unit
) : RecyclerView.Adapter<ReservesAdapter.ReserveViewHolder>() {

    inner class ReserveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtEventName: TextView = itemView.findViewById(R.id.eventName)
        private val btnCancel: Button = itemView.findViewById(R.id.cancelButton)

        fun bind(reserve: ReserveWithEvent) {
            txtEventName.text = reserve.eventName.ifEmpty { "Evento sin nombre" }

            reserve.idReserve?.let { id ->
                btnCancel.visibility = View.VISIBLE
                btnCancel.setOnClickListener { onCancelClick(id) }
            } ?: run {
                btnCancel.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReserveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserve, parent, false)
        return ReserveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReserveViewHolder, position: Int) {
        holder.bind(reserves[position])
    }

    override fun getItemCount(): Int = reserves.size

    fun updateReserves(newReserves: List<ReserveWithEvent>) {
        reserves.clear()
        reserves.addAll(newReserves)
        notifyDataSetChanged()
    }
}
package com.example.culturewaveinter.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.Reserve
import com.example.culturewaveinter.Entities.ReserveWithEvent
import com.example.culturewaveinter.R

class ReservesAdapter(
    private val reserves: List<Reserve>,
    private val onCancelClick: (Int) -> Unit
                     ) : RecyclerView.Adapter<ReservesAdapter.ReserveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReserveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserve, parent, false)
        return ReserveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReserveViewHolder, position: Int) {
        val reserve = reserves[position]
        holder.bind(reserve)
    }

    override fun getItemCount(): Int = reserves.size

    inner class ReserveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val eventName: TextView = view.findViewById(R.id.eventName) // Añade estas líneas
        private val cancelButton: Button = view.findViewById(R.id.cancelButton)

        fun bind(reserve: Reserve) {
            eventName.text = reserve.idEvent.toString()
            cancelButton.setOnClickListener {
                onCancelClick(reserve.id)
            }
        }
    }
}

package com.example.culturewaveinter.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.CalendarDay
import com.example.culturewaveinter.R

class CalendarAdapter(
    private val daysList: List<CalendarDay>,
    private val onDayClickListener: (CalendarDay) -> Unit
                     ) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_day_item, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val currentDay = daysList[position]
        holder.dayTextView.text = currentDay.day.toString()
        holder.eventCircle.visibility = if (currentDay.events.isNotEmpty()) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (currentDay.events.isNotEmpty()) onDayClickListener(currentDay)
        }
    }

    override fun getItemCount() = daysList.size

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayText)
        val eventCircle: View = itemView.findViewById(R.id.eventCircle)
    }
}
package com.example.culturewaveinter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.R
import java.time.format.DateTimeFormatter

class EventAdapter(private val eventList: List<Event>, private val spaceList: List<Space>) :
        RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

            class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nombreEvento: TextView = itemView.findViewById(R.id.textNameEvent)
            val sala: TextView = itemView.findViewById(R.id.textSpace)
            val fechaHora: TextView = itemView.findViewById(R.id.textDateTime)
            val descripcion: TextView = itemView.findViewById(R.id.textDescription)
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false)
            return EventViewHolder(view)
            }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
            val evento = eventList[position]
            holder.nombreEvento.text = evento.name
            val space = spaceList.find {it.id == evento.Space}
            holder.sala.text = space?.name?: "Uknown"
            holder.fechaHora.text = evento.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))
            holder.descripcion.text = evento.description
            }

        override fun getItemCount(): Int = eventList.size
}
package com.example.culturewaveinter.Adapters

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import com.example.culturewaveinter.FragmentActivity
import com.example.culturewaveinter.FragmentEvents
import com.example.culturewaveinter.R
import java.time.format.DateTimeFormatter

class EventAdapter(private val eventList: List<Event>, private val spaceList: List<Space>, private val currentUser: User) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreEvento: TextView = itemView.findViewById(R.id.textNameEvent)
        val sala: TextView = itemView.findViewById(R.id.textSpace)
        val fechaHora: TextView = itemView.findViewById(R.id.textDateTime)
        val descripcion: TextView = itemView.findViewById(R.id.textDescription)
        val btnInformation: View = itemView.findViewById(R.id.btnInformation)  // Obtenemos el botón directamente
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.nombreEvento.text = event.name
        val space = spaceList.find { it.id == event.idSpace }
        holder.sala.text = space?.name ?: "Unknown"
        holder.fechaHora.text = event.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"))
        holder.descripcion.text = event.description

        holder.btnInformation.setOnClickListener {
            val fragment = FragmentEvents()

            val bundle = Bundle()
            bundle.putSerializable("event", event)
            bundle.putSerializable("spaces", ArrayList(spaceList))
            bundle.putSerializable("user", currentUser)

            fragment.arguments = bundle

            val fragmentManager = (holder.itemView.context as? FragmentActivity)?.supportFragmentManager

            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainer, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }




    }

    override fun getItemCount(): Int = eventList.size
}
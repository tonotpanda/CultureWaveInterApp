package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FragmentEvents : Fragment(R.layout.fragmentevents) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = arguments?.getSerializable("event") as? Event ?: return
        val spaces = arguments?.getSerializable("spaces") as? ArrayList<Space> ?: arrayListOf()
        val user = arguments?.getSerializable("user") as? User ?: return

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        view.findViewById<TextView>(R.id.eventTitle).text = event.name
        view.findViewById<TextView>(R.id.eventDescription).text = event.description
        view.findViewById<TextView>(R.id.eventDate).text = event.startDate.format(dateFormatter)
        view.findViewById<TextView>(R.id.eventTime).text =
            "${event.startDate.format(timeFormatter)} - ${event.endDate.format(timeFormatter)}"

        val space = spaces.find { it.id == event.idSpace }
        view.findViewById<TextView>(R.id.spaceEvent).text = space?.name ?: "Espacio desconocido"

        val currentMonth = LocalDate.now().monthValue
        val eventMonth = event.startDate.monthValue
        val monthIndicator = if (currentMonth == eventMonth) "Evento de este mes" else "Evento de otro mes"
        view.findViewById<TextView>(R.id.eventMonthIndicator).text = monthIndicator

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.btnReservar).setOnClickListener{
            val fragment = FragmentReserveEvent.newInstance(
                event,
                user,
                space?.name ?: "Desconocido"
            )

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}

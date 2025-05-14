package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FragmentEvents : Fragment(R.layout.fragmentevents) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener datos del Bundle
        val event = arguments?.getSerializable("event") as? Event ?: return
        val spaces = arguments?.getSerializable("spaces") as? ArrayList<Space> ?: arrayListOf()

        // Formateadores de fecha y hora
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        // Vincular datos a las vistas
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

        // Botón de regreso
        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}

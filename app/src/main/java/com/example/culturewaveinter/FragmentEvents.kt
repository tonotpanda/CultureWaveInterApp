package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.Event
import java.time.format.DateTimeFormatter

class FragmentEvents : Fragment(R.layout.fragmentevents) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = arguments?.getSerializable("event") as? Event ?: return

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        view.findViewById<TextView>(R.id.textNameEvent).text = event.name
        view.findViewById<TextView>(R.id.editTextDescripcionEvento).text = event.description
        view.findViewById<TextView>(R.id.eventDate).text = event.startDate.format(dateFormatter)
        view.findViewById<TextView>(R.id.eventTime).text =
            "${event.startDate.format(timeFormatter)} - ${event.endDate.format(timeFormatter)}"

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
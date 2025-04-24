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
import java.time.format.DateTimeFormatter

class FragmentEvents : Fragment(R.layout.fragmentevents) {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = arguments?.getSerializable("event") as? Event ?: return
        val spaceList = arguments?.getSerializable("spaces") as? ArrayList<Space> ?: arrayListOf()

        val formatterDate = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

        view.findViewById<TextView>(R.id.eventTitle).text = event.name
        view.findViewById<TextView>(R.id.eventDescription).text = event.description
        view.findViewById<TextView>(R.id.eventDate).text = event.startDate.format(formatterDate)
        view.findViewById<TextView>(R.id.eventTime).text = "${event.startDate.format(formatterTime)} - ${event.endDate.format(formatterTime)}"
        val space = spaceList.find {it.id == event.idSpace}
        view.findViewById<TextView>(R.id.spaceEvent).text = space?.name?: "Unknown"

        view.findViewById<ImageView>(R.id.back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
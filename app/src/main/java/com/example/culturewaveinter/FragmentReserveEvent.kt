package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.User
import java.time.format.DateTimeFormatter

class FragmentReserveEvent: Fragment(R.layout.fragmentreserveevents) {

    companion object {
        fun newInstance(event: Event, user: User, spaceName: String): FragmentReserveEvent {
            val fragment= FragmentReserveEvent()
            val args = Bundle()
            args.putSerializable("event", event)
            args.putSerializable("user", user)
            args.putString("spaceName", spaceName)
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event = arguments?.getSerializable("event") as? Event
        val user = arguments?.getSerializable("user") as? User
        val spaceName = arguments?.getString("spaceName")

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        view.findViewById<TextView>(R.id.eventTitle).text = event?.name
        view.findViewById<TextView>(R.id.spaceEvent).text = spaceName
        view.findViewById<TextView>(R.id.eventDate).text = event?.startDate?.format(dateFormatter)
        view.findViewById<TextView>(R.id.eventTime).text =
            "${event?.startDate?.format(timeFormatter)} - ${event?.endDate?.format(timeFormatter)}"


    }

}
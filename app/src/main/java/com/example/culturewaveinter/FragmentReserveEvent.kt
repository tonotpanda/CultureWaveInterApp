package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.User
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

class FragmentReserveEvent: Fragment(R.layout.fragmentreserveevents) {

    companion object {
        fun newInstance(event: Event, user: User, spaceName: String, spaceCapacity: Int): FragmentReserveEvent {
            val fragment= FragmentReserveEvent()
            val args = Bundle()
            args.putSerializable("event", event)
            args.putSerializable("user", user)
            args.putString("spaceName", spaceName)
            args.putInt("spaceCapacity", spaceCapacity)
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
        val spaceCapacity = arguments?.getInt("spaceCapacity")

        val eventTitle = view.findViewById<TextView>(R.id.eventTitle)
        val spaceEvent = view.findViewById<TextView>(R.id.spaceEvent)
        val eventDate = view.findViewById<TextView>(R.id.eventDate)
        val eventTime = view.findViewById<TextView>(R.id.eventTime)
        val spinnerColumn = view.findViewById<Spinner>(R.id.spinnerColumn)
        val spinnerRow = view.findViewById<Spinner>(R.id.spinnerRow)

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        event?.let {
            eventTitle.text = it.name
            spaceEvent.text = spaceName
            eventDate.text = it.startDate.format(dateFormatter)
            eventTime.text = "${it.startDate.format(timeFormatter)} - ${it.endDate.format(timeFormatter)}"

            val totalCapacity = spaceCapacity?.toDouble() ?: 0.0
            val numColumns = ceil(totalCapacity / 10.0).toInt()

            val columLetters = ('A'..'Z').take(numColumns).map { it.toString() }
            
            val columnAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, columLetters)
            columnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerColumn.adapter = columnAdapter

            spinnerColumn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val rowsInColum = if (position == numColumns -1 && totalCapacity % 10 != 0.0) {
                        (totalCapacity % 10).toInt()
                    } else {
                        10
                    }

                    val rows = (1..rowsInColum).map {it.toString()}
                    val rowAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, rows)
                    rowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerRow.adapter = rowAdapter
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            spinnerColumn.setSelection(0)
        }
    }

}
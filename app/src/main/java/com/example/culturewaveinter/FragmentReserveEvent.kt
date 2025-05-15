package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Reserve
import com.example.culturewaveinter.Entities.Seat
import com.example.culturewaveinter.Entities.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.ceil

class FragmentReserveEvent : Fragment(R.layout.fragmentreserveevents) {

    companion object {
        fun newInstance(
            event: Event,
            user: User,
            spaceName: String,
            spaceCapacity: Int
                       ): FragmentReserveEvent {
            return FragmentReserveEvent().apply {
                arguments = Bundle().apply {
                    putSerializable("event", event)
                    putSerializable("user", user)
                    putString("spaceName", spaceName)
                    putInt("spaceCapacity", spaceCapacity)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnReserve    = view.findViewById<Button>(R.id.btnReservar)
        val spinnerColumn = view.findViewById<Spinner>(R.id.spinnerColumn)
        val spinnerRow    = view.findViewById<Spinner>(R.id.spinnerRow)
        val tvTitle       = view.findViewById<TextView>(R.id.eventTitle)
        val tvSpace       = view.findViewById<TextView>(R.id.spaceEvent)
        val tvDate        = view.findViewById<TextView>(R.id.eventDate)
        val tvTime        = view.findViewById<TextView>(R.id.eventTime)

        // Extraemos argumentos
        val event         = arguments?.getSerializable("event") as? Event
        val user          = arguments?.getSerializable("user") as? User
        val spaceName     = arguments?.getString("spaceName") ?: ""
        val spaceCapacity = arguments?.getInt("spaceCapacity") ?: 0

        // Pintamos datos del evento
        event?.let {
            tvTitle.text = it.name
            tvSpace.text = spaceName
            tvDate.text  = it.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            tvTime.text  = "${it.startDate.format(DateTimeFormatter.ofPattern("HH:mm"))} - " +
                    "${it.endDate.format(DateTimeFormatter.ofPattern("HH:mm"))}"

            // Calculamos columnas (letras)
            val totalCap = spaceCapacity.toDouble()
            val numCols  = ceil(totalCap / 10.0).toInt()
            val cols     = ('A'..'Z').take(numCols).map { it.toString() }

            spinnerColumn.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cols
                                                ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            spinnerColumn.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    v: android.view.View?,
                    position: Int,
                    id: Long
                                           ) {
                    val rowsInLastCol = (totalCap % 10).toInt()
                        .takeIf { position == numCols - 1 && it > 0 } ?: 10
                    val rows = (1..rowsInLastCol).map { it.toString() }
                    spinnerRow.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        rows
                                                     ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            spinnerColumn.setSelection(0)
        }

        // Al pulsar Reservar
        btnReserve.setOnClickListener {
            if (event == null || user == null) {
                Toast.makeText(requireContext(), "Evento o usuario inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val col = spinnerColumn.selectedItem?.toString()?.take(1)
            val row = spinnerRow.selectedItem?.toString()?.toIntOrNull()
            if (col.isNullOrEmpty() || row == null) {
                Toast.makeText(requireContext(), "Selecciona fila y columna", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // 1) Creamos la reserva
                val reserveReq = Reserve(id = 0, reserveDate = Date(), idEvent = event.idEvent)
                val reserveRes = ApiRepository.createReserve(reserveReq)
                if (reserveRes == null) {
                    Toast.makeText(requireContext(), "Error creando reserva", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 2) Creamos el asiento
                val seatReq = Seat(
                    id        = 0,
                    row       = col[0],  // Char
                    numSeat   = row,
                    idSpace   = event.idSpace,
                    idReserve = reserveRes.id
                                  )
                val seatRes = ApiRepository.createSeat(seatReq)
                if (seatRes == null) {
                    Toast.makeText(requireContext(), "Error creando asiento", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 3) Aviso de confirmación
                Toast.makeText(requireContext(),
                               "Reserva realizada con éxito. ¡Disfruta tu evento!",
                               Toast.LENGTH_LONG).show()

                // 4) Volvemos al Home
                (requireActivity() as FragmentActivity).apply {
                    loadFragment(FragmentHome.newInstance(user))
                    findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        .selectedItemId = R.id.navigation_home
                }
            }
        }
    }
}

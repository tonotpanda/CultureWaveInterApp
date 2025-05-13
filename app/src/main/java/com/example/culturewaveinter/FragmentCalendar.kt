package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Adapters.CalendarAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.CalendarDay
import com.example.culturewaveinter.Entities.Event
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class FragmentCalendar : Fragment(R.layout.fragmentcalendar) {

    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var monthYearText: TextView
    private lateinit var prevMonthBtn: Button
    private lateinit var nextMonthBtn: Button
    private var currentDate = Calendar.getInstance()
    private var allEvents: List<Event> = emptyList() // Eventos cargados desde la API

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        monthYearText = view.findViewById(R.id.monthYearTitle)
        prevMonthBtn = view.findViewById(R.id.prevMonthButton)
        nextMonthBtn = view.findViewById(R.id.nextMonthButton)

        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        loadCalendar()

        prevMonthBtn.setOnClickListener {
            currentDate.add(Calendar.MONTH, -1)
            loadCalendar()
        }

        nextMonthBtn.setOnClickListener {
            currentDate.add(Calendar.MONTH, 1)
            loadCalendar()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadCalendar() {
        lifecycleScope.launch {
            try {
                // 1. Obtener eventos desde la API
                val events = ApiRepository.getEvents()
                if (events != null) {
                    allEvents = events
                    // 2. Generar días del calendario con eventos reales
                    val calendarDays = generateCalendarDays()
                    // 3. Actualizar UI en el hilo principal
                    requireActivity().runOnUiThread {
                        monthYearText.text = formatMonthYear()
                        calendarRecyclerView.adapter = CalendarAdapter(calendarDays) { selectedDay ->
                            if (selectedDay.events.isNotEmpty()) {
                                openEventsFragment(selectedDay)
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatMonthYear(): String {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1
        return YearMonth.of(year, month).format(
            DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                                               )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateCalendarDays(): List<CalendarDay> {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1
        val yearMonth = YearMonth.of(year, month)

        return (1..yearMonth.lengthOfMonth()).map { day ->
            val date = yearMonth.atDay(day)
            CalendarDay(
                day = day,
                date = date,
                isCurrentMonth = true,
                events = getEventsForDay(date)
                       )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEventsForDay(date: LocalDate): List<Event> {
        return allEvents.filter { event ->
            event.startDate.toLocalDate() == date
        }
    }

    private fun openEventsFragment(selectedDay: CalendarDay) {
        val fragment = FragmentEvents().apply {
            arguments = Bundle().apply {
                putSerializable("event", selectedDay.events.first())
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
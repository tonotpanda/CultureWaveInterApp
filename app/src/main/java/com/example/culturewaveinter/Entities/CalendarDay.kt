package com.example.culturewaveinter.Entities

import java.time.LocalDate

data class CalendarDay(
    val day: Int,
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val events: List<Event>
                      )
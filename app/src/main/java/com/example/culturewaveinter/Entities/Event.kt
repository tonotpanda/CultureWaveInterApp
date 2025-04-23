package com.example.culturewaveinter.Entities

import java.time.LocalDateTime


class Event (
    val id: Int,
    val name: String,
    val description: String,
    val capacity: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: String,
    val Space: Int
)
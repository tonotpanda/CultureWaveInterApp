package com.example.culturewaveinter.Entities

import java.time.LocalDateTime
import java.io.Serializable


class Event(
    val id: String,
    val name: String,
    val description: String,
    val capacity: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val status: String,
    val idSpace: Int
           ) : Serializable
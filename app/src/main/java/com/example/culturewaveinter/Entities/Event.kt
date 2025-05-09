package com.example.culturewaveinter.Entities

import java.time.LocalDateTime
import java.io.Serializable


class Event(
    val id: String,
    var name: String,
    var description: String,
    var capacity: Int,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime,
    var status: String,
    var idSpace: Int
           ) : Serializable
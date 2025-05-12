package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.io.Serializable


class Event(
    @SerializedName("idEvent") val idEvent: Int,
    var idSpace: Int,
    var name: String,
    var description: String,
    var capacity: Int,
    var startDate: LocalDateTime,
    var endDate: LocalDateTime,
    var status: String

           ) : Serializable

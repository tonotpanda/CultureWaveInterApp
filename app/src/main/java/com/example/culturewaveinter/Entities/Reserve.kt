package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Reserve(
    @SerializedName("idReserve") val id: Int,
    @SerializedName("reserveDate") val reserveDate: Date,
    @SerializedName("idEvent") val idEvent: Int,
    @SerializedName("eventTable") val event: Event // Añade esta línea
                  )
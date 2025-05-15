package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName

data class Seat(
    @SerializedName("idSeat")    val id: Int,
    @SerializedName("row")       val row: Char,   // Ahora Char
    @SerializedName("numSeat")   val numSeat: Int,
    @SerializedName("idSpace")   val idSpace: Int,
    @SerializedName("idReserve") val idReserve: Int
               )

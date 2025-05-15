package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName

class Seat (
    @SerializedName("idSeat") val id: Int,
    val row: Char,
    val numSeat: Int,
    val idSpace: Int,
    val idReserve: Int
)
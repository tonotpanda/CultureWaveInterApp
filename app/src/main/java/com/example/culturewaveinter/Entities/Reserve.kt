package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Reserve (
    @SerializedName("idReserve")val id: Int,
    val reserveDate: Date,
    val idEvent: Int
)
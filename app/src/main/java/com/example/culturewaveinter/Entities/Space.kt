package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Space (
    @SerializedName("idSpace") val id: Int,
    var name: String,
    var capacity: Int,
    var fixedSeats: Boolean,
    var available: Boolean
) : Serializable
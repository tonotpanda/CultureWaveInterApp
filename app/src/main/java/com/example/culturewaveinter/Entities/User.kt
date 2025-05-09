package com.example.culturewaveinter.Entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class User (
    @SerializedName("idUser") val id: Int,
    var name: String,
    var email: String,
    var password: String,
    val rol: Int
) : Serializable

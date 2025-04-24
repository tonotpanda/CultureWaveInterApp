package com.example.culturewaveinter.Entities

import java.io.Serializable

class Space (
    val id: Int,
    val name: String,
    val capacity: Int,
    val fixedSeats: Int,
    val available: Boolean
) : Serializable
package com.example.culturewaveinter.Entities

import java.util.Date

class Ticket (
    val id: Int,
    val type: String,
    val status: String,
    val description: String,
    val creationDate: Date,
    val idReserve: Int,
)
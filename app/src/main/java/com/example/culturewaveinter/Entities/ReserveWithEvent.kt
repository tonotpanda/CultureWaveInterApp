package com.example.culturewaveinter.Entities

data class ReserveWithEvent(
    val idReserve: Int,
    val eventName: String,  // Nombre del evento relacionado con la reserva
    val idEvent: Int,  // ID del evento
                           )

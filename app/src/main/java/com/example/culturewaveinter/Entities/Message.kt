package com.example.culturewaveinter.Entities

import java.sql.Timestamp

class Message (
    val id: Int,
    val text: String,
    val timestamp: Timestamp,
    val idUser: Int
)
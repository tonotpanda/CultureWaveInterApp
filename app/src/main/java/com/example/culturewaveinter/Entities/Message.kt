package com.example.culturewaveinter.Entities

data class Message(
    val id: Int,
    val from: Int,
    val content: String,
    val timestamp: String,
    var senderName: String = ""
                  )

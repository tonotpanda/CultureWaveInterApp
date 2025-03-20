package com.example.culturewaveinter.Entities

import java.io.Serializable


data class User (
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val rol: Int
) : Serializable

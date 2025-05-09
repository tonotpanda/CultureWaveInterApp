package com.example.culturewaveinter.Api

import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("spaces")
    suspend fun getSpaces(): Response<List<Space>>

    @GET("eventTables")
    suspend fun getEvents(): Response<List<Event>>
}
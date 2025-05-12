package com.example.culturewaveinter.Api

import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("spaces")
    suspend fun getSpaces(): Response<List<Space>>

    @GET("eventTables")
    suspend fun getEvents(): Response<List<Event>>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body user: User
                          ): Response<Void>

    @POST("eventTables")
    suspend fun createEvent(@Body event: Event): Response<Void>  // Respuesta vacía porque solo estamos creando el evento



}
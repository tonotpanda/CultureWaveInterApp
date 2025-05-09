package com.example.culturewaveinter.Api

import com.example.culturewaveinter.Entities.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<User>>
}
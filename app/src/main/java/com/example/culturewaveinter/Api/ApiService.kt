package com.example.culturewaveinter.Api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.culturewaveinter.Api.ApiClient.apiService
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Reserve
import com.example.culturewaveinter.Entities.Seat
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    @POST("users")
    suspend fun createUser(@Body user: User): Response<User>

    @POST("eventTables")
    suspend fun createEvent(@Body event: Event): Response<Event>

    @POST("reserves")
    suspend fun createReserve(@Body reserve: Reserve): Response<Reserve>

    @POST("seats")
    suspend fun createSeat(@Body seat: Seat): Response<Seat>

}
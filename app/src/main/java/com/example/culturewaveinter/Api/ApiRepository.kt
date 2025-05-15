package com.example.culturewaveinter.Api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Reserve
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.GET

object ApiRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    private val apiService = ApiClient.apiService

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUsers(): List<User>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUsers()
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getSpaces(): List<Space>? {
        return withContext(Dispatchers.IO){
            val response = apiService.getSpaces()
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEvents(): List<Event>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEvents()
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateUser(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateUser(user.id, user)
            response.isSuccessful
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createUser(user: User): User? {
        return withContext(Dispatchers.IO) {
            val response = apiService.createUser(user)
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createEvent(event: Event): Event? {
        return withContext(Dispatchers.IO) {
            val response = apiService.createEvent(event)
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllUsers(): List<User>? {
        return withContext(Dispatchers.IO) {
            val response = ApiClient.apiService.getUsers()
            if (response.isSuccessful) {
                return@withContext response.body()
            } else {
                return@withContext null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getUserReserves(userId: Int): List<Reserve>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUserReserves(userId)
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelReserve(reserveId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.cancelReserve(reserveId)
            response.isSuccessful
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventById(eventId: Int): Event? {
        val response = apiService.getEventById(eventId)
        return if (response.isSuccessful) {
            response.body()  // Retorna el cuerpo de la respuesta (evento)
        } else {
            null  // En caso de error, retorna null
        }
    }
}
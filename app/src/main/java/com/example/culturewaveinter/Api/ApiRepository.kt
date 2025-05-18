package com.example.culturewaveinter.Api

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Reserve
import com.example.culturewaveinter.Entities.ReserveResponse
import com.example.culturewaveinter.Entities.Seat
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    suspend fun createReserve(reserve: Reserve): Reserve? {
        return withContext(Dispatchers.IO) {
            val response = apiService.createReserve(reserve)
            if (response.isSuccessful) response.body() else null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createSeat(seat: Seat): Seat? = withContext(Dispatchers.IO) {
        val response = apiService.createSeat(seat)
        if (response.isSuccessful) {
            response.body()
        } else {
            val err = response.errorBody()?.string()
            Log.e("ApiRepository", "createSeat fallo: HTTP ${response.code()} – $err")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getReservesByUser(userId: Int): List<ReserveResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiClient.apiService.getReservesByUser(userId)
                if (response.isSuccessful) {
                    response.body().also {
                        Log.d("API_RESPONSE", "Reservas recibidas: ${it?.size}")
                    }
                } else {
                    Log.e("API_ERROR", "Código: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", e.message ?: "Error desconocido")
                null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelReserve(reserveId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.cancelReserve(reserveId)
            response.isSuccessful
        }
    }
}
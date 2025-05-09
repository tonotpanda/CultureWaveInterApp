package com.example.culturewaveinter.Api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.culturewaveinter.Entities.Event
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


}
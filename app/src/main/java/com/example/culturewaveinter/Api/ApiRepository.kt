package com.example.culturewaveinter.Api

import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiRepository {

    private val apiService = ApiClient.apiService

    suspend fun getUsers(): List<User>? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getUsers()
            if (response.isSuccessful) response.body() else null
        }
    }

}
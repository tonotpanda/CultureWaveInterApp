package com.example.culturewaveinter.Api

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.culturewaveinter.Adapters.LocalDateTimeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object ApiClient {

    private const val BASE_URL = "http://10.0.0.174/ApiCultureWave/api/"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun provideGson(): Gson {
        val localDateTimeAdapter = LocalDateTimeAdapter()

        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")     // <— Acepta timestamps sin 'Z'
            .registerTypeAdapter(LocalDateTime::class.java, localDateTimeAdapter)
            .create()
    }

    val apiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
            .create(ApiService::class.java)
    }
}

package com.example.culturewaveinter.Api

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object ApiClient {

    private const val BASE_URL = "http://10.0.0.174/ApiCultureWave/api/"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun provideGson(): Gson {
        val localDateTimeDeserializer = JsonDeserializer<LocalDateTime> { json, _, _ ->
            LocalDateTime.parse(json.asString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, localDateTimeDeserializer)
                .create()
        } else {
            GsonBuilder().create()
        }
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(provideGson()))
            .build()
            .create(ApiService::class.java)
    }
}

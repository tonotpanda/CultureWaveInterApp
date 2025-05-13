package com.example.culturewaveinter.Entities

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Event(
    @SerializedName("idEvent") val idEvent: Int,
    var idSpace: Int,
    var name: String,
    var description: String,
    var capacity: Int,
    @SerializedName("startDate") var startDate: LocalDateTime,
    @SerializedName("endDate") var endDate: LocalDateTime,
    var status: String
                ) : java.io.Serializable
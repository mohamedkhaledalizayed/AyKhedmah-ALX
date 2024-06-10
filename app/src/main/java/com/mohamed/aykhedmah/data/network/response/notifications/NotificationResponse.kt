package com.mohamed.aykhedmah.data.network.response.notifications


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class NotificationResponse(
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
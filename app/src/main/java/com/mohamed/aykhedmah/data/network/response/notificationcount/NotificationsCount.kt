package com.mohamed.aykhedmah.data.network.response.notificationcount


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotificationsCount(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
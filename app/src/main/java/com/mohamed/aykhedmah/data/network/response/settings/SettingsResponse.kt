package com.mohamed.aykhedmah.data.network.response.settings


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SettingsResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
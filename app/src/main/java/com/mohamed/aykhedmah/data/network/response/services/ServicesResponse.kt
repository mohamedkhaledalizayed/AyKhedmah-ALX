package com.mohamed.aykhedmah.data.network.response.services


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ServicesResponse(
    @SerializedName("data")
    val `data`: List<ServicesData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
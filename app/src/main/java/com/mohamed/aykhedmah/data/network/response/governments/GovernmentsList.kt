package com.mohamed.aykhedmah.data.network.response.governments


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GovernmentsList(
    @SerializedName("data")
    val `data`: List<Governorate>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
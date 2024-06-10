package com.mohamed.aykhedmah.data.network.response.slider


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SliderResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
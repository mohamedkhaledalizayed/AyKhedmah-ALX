package com.mohamed.aykhedmah.data.network.response.cities


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CitiesResponse(
    @SerializedName("data")
    val `data`: List<CityData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
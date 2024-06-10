package com.mohamed.aykhedmah.data.network.response.citieslist


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CitiesList(
    @SerializedName("data")
    val `data`: List<City>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
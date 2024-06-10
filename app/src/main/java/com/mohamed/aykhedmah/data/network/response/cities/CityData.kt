package com.mohamed.aykhedmah.data.network.response.cities


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class CityData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_shown")
    val isShown: String,
    @SerializedName("title")
    val title: String
)
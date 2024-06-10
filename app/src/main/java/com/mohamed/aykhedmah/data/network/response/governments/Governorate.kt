package com.mohamed.aykhedmah.data.network.response.governments


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Governorate(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_shown")
    val isShown: String,
    @SerializedName("title")
    val title: String
)
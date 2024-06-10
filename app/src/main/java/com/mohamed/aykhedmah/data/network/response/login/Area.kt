package com.mohamed.aykhedmah.data.network.response.login


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Area(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_shown")
    val isShown: String,
    @SerializedName("title")
    val title: String
)
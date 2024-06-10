package com.land.services.data.model.response.search


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class Area(
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_shown")
    val isShown: String,
    @SerializedName("title")
    val title: String
)
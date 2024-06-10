package com.mohamed.aykhedmah.data.network.response.slider


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("user_data")
    val userData: Any,
    @SerializedName("user_id")
    val userId: Any
)
package com.mohamed.aykhedmah.data.network.response.deleteimage


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Data(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("user_id")
    val userId: Int
)
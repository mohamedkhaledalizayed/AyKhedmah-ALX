package com.mohamed.aykhedmah.data.network.response.updateprofile


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class UserImage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("user_id")
    val userId: Int
)
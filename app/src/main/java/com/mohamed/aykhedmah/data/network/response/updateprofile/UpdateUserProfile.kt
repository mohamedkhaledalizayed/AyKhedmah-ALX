package com.mohamed.aykhedmah.data.network.response.updateprofile


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class UpdateUserProfile(
    @SerializedName("data")
    val data: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
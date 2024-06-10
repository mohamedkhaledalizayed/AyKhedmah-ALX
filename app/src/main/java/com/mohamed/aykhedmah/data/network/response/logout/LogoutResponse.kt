package com.mohamed.aykhedmah.data.network.response.logout


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class LogoutResponse(
    @SerializedName("data")
    val `data`: Any,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
package com.mohamed.aykhedmah.data.network.response.arealist


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AreasList(
    @SerializedName("data")
    val `data`: List<AreaData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
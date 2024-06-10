package com.land.services.data.model.response.search


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class SearchResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)
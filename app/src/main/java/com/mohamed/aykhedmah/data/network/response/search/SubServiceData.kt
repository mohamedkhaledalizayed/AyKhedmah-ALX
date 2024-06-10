package com.land.services.data.model.response.search


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class SubServiceData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_show")
    val isShow: String,
    @SerializedName("level")
    val level: Int,
    @SerializedName("perant_id")
    val perantId: Int,
    @SerializedName("service_provider_count")
    val serviceProviderCount: Int,
    @SerializedName("sub_service_provider_count")
    val subServiceProviderCount: Int,
    @SerializedName("title")
    val title: String
)
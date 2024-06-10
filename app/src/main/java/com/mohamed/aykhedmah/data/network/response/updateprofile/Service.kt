package com.mohamed.aykhedmah.data.network.response.updateprofile


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Service(
    @SerializedName("id")
    val id: Int,
    @SerializedName("service_data")
    val serviceData: ServiceData,
    @SerializedName("service_id")
    val serviceId: Int,
    @SerializedName("sub_service_data")
    val subServiceData: SubServiceData,
    @SerializedName("sub_service_id")
    val subServiceId: Int,
    @SerializedName("user_id")
    val userId: Int
)
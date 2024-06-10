package com.land.services.data.model.response.search


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class Data(
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
    @SerializedName("user_data")
    val userData: UserData,
    @SerializedName("user_id")
    val userId: Int
)
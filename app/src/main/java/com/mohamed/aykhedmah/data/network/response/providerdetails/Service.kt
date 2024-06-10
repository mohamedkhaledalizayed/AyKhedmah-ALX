package com.mohamed.aykhedmah.data.network.response.providerdetails

import androidx.annotation.Keep

@Keep
data class Service(
    val id: Int,
    val service_data: ServiceData,
    val service_id: Int,
    val sub_service_data: SubServiceData,
    val sub_service_id: Int,
    val user_id: Int
)
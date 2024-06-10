package com.mohamed.aykhedmah.data.network.response.signupprovider

import androidx.annotation.Keep

@Keep
data class Service(
    val id: Int,
    val service_data: ServiceData,
    val service_id: Int,
    val sub_service_data: Any,
    val sub_service_id: Any,
    val user_id: Int
)
package com.mohamed.aykhedmah.data.network.response.providers

import androidx.annotation.Keep

@Keep
data class Data(
    val id: Int,
    val service_id: Int,
    val sub_service_id: Int,
    val user_data: UserData,
    val user_id: Int
)
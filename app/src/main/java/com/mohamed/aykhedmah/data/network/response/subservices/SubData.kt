package com.mohamed.aykhedmah.data.network.response.subservices

import androidx.annotation.Keep

@Keep
data class SubData(
    val id: Int,
    val image: String,
    val is_show: String,
    val level: Int,
    val perant_id: Int,
    val sub_service_count: Int,
    val sub_service_provider_count: Int,
    val title: String
)
package com.mohamed.aykhedmah.data.network.response.services

import androidx.annotation.Keep

@Keep
data class ServicesData(
    val id: Int,
    val image: String,
    val is_show: String,
    val level: Int,
    val perant_id: Int,
    val service_provider_count: Int,
    val title: String
)
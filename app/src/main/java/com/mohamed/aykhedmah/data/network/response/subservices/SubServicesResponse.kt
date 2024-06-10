package com.mohamed.aykhedmah.data.network.response.subservices

import androidx.annotation.Keep

@Keep
data class SubServicesResponse(
    val `data`: List<SubData>,
    val message: String,
    val status: Int
)
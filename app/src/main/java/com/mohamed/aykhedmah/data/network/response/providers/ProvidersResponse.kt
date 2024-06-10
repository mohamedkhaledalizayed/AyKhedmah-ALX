package com.mohamed.aykhedmah.data.network.response.providers

import androidx.annotation.Keep

@Keep
data class ProvidersResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)
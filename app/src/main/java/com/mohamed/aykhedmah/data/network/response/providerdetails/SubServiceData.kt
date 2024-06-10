package com.mohamed.aykhedmah.data.network.response.providerdetails

import androidx.annotation.Keep

@Keep
data class SubServiceData(
    val id: Int,
    val image: String,
    val is_show: String,
    val level: Int,
    val perant_id: Int,
    val title: String
)
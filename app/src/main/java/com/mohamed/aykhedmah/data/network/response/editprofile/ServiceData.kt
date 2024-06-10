package com.mohamed.aykhedmah.data.network.response.editprofile

import androidx.annotation.Keep

@Keep
data class ServiceData(
    val id: Int,
    val image: String,
    val is_show: String,
    val level: Int,
    val perant_id: Any,
    val title: String
)
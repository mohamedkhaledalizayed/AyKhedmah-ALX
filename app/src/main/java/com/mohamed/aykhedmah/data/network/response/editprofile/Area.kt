package com.mohamed.aykhedmah.data.network.response.editprofile

import androidx.annotation.Keep

@Keep
data class Area(
    val id: Int,
    val is_shown: String,
    val title: String
)
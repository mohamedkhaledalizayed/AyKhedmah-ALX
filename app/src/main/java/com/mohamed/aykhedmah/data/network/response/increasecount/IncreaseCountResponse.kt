package com.mohamed.aykhedmah.data.network.response.increasecount

import androidx.annotation.Keep

@Keep
data class IncreaseCountResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
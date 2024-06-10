package com.mohamed.aykhedmah.data.network.response.providerdetails

import androidx.annotation.Keep

@Keep
data class ProfileResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
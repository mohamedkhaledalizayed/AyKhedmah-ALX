package com.mohamed.aykhedmah.data.network.response.signupprovider

import androidx.annotation.Keep

@Keep
data class ProviderSignUpResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
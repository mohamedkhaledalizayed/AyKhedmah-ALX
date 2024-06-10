package com.mohamed.aykhedmah.data.network.response.contactus

import androidx.annotation.Keep

@Keep
data class ContactUsResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
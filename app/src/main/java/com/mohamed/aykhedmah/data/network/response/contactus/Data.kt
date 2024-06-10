package com.mohamed.aykhedmah.data.network.response.contactus

import androidx.annotation.Keep

@Keep
data class Data(
    val created_at: String,
    val details: String,
    val id: Int,
    val name: String,
    val phone: String,
    val status: String,
    val subject: String,
    val updated_at: String
)
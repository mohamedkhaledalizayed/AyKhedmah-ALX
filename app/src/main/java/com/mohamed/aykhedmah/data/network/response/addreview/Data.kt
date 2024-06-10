package com.mohamed.aykhedmah.data.network.response.addreview

import androidx.annotation.Keep

@Keep
data class Data(
    val comment: String,
    val created_at: String,
    val from_user_id: Int,
    val id: Int,
    val order_id: Any,
    val rate: Float,
    val reason: Any,
    val status: String,
    val to_user_id: Int,
    val type: Any,
    val updated_at: String
)
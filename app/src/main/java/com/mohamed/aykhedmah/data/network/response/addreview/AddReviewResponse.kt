package com.mohamed.aykhedmah.data.network.response.addreview

import androidx.annotation.Keep

@Keep
data class AddReviewResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
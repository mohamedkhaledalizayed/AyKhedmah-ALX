package com.mohamed.aykhedmah.data.network.response.reviews

import androidx.annotation.Keep

@Keep
data class ReviewsResponse(
    val `data`: List<Data>,
    val message: String,
    val status: Int
)
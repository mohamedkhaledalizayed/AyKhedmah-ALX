package com.mohamed.aykhedmah.data.network.response.providerdetails

import androidx.annotation.Keep

@Keep
data class UserImage(
    val id: Int,
    val image: String,
    val user_id: Int
)
package com.mohamed.aykhedmah.data.network.response.editprofile

import androidx.annotation.Keep

@Keep
data class EditUserProfileResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
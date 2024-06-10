package com.mohamed.aykhedmah.data.network.response.signupuser

import androidx.annotation.Keep

@Keep
data class UserSignUpResponse(
    val `data`: Data,
    val message: String,
    val status: Int
)
package com.mohamed.aykhedmah.data.network.response.providers

import androidx.annotation.Keep

@Keep
data class UserData(
    val address: Any,
    val area: Area,
    val area_id: Int,
    val banner: Any,
    val confirmed_code: Any,
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val forget_password_code: Any,
    val id: Int,
    val is_block: String,
    val is_confirmed: String,
    val is_login: String,
    val last_logout: Int,
    val latitude: Double,
    val logo: String,
    val longitude: Double,
    val name: String,
    val phone: String,
    val phone_code: String,
    val rate: Float,
    val refuse_reason: Any,
    val software_type: String,
    val updated_at: String,
    val user_type: String,
    val zone: String
)
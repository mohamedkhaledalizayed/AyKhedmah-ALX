package com.mohamed.aykhedmah.data.network.response.providerdetails

import androidx.annotation.Keep

@Keep
data class Data(
    val address: Any,
    val area: Area,
    val area_id: Int,
    val banner: Any,
    val confirmed_code: Any,
    val created_at: String,
    val details: Details?,
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
    val view_count: Int,
    val call_count: Int,
    val longitude: Double,
    val name: String,
    val note: String,
    val phone: String,
    val phone_code: String,
    val rate: Float,
    val refuse_reason: Any,
    val service: List<Service>,
    val software_type: String,
    val token: String,
    val updated_at: String,
    val user_images: List<UserImage>,
    val user_type: String,
    val zone: String
)
package com.mohamed.aykhedmah.data.network.response.signupuser

import androidx.annotation.Keep
import com.mohamed.aykhedmah.data.network.response.citieslist.City
import com.mohamed.aykhedmah.data.network.response.governments.Governorate

@Keep
data class Data(
    val address: Any,
    val area: Area,
    val banner: Any,
    val confirmed_code: Any,
    val created_at: String,
    val details: Any,
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
    val service: List<Any>,
    val governorate: Governorate,
    val city: City,
    val software_type: String,
    val token: String,
    val updated_at: String,
    val user_type: String,
    val note: String,
    val zone: String
)
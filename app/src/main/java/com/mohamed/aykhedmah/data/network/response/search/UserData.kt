package com.land.services.data.model.response.search


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
class UserData(
    @SerializedName("address")
    val address: String,
    @SerializedName("area")
    val area: Area,
    @SerializedName("area_id")
    val areaId: Int,
    @SerializedName("banner")
    val banner: Any,
    @SerializedName("call_count")
    val callCount: Int,
    @SerializedName("confirmed_code")
    val confirmedCode: Any,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any,
    @SerializedName("forget_password_code")
    val forgetPasswordCode: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_block")
    val isBlock: String,
    @SerializedName("is_confirmed")
    val isConfirmed: String,
    @SerializedName("is_login")
    val isLogin: String,
    @SerializedName("last_logout")
    val lastLogout: Int,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("logo")
    val logo: String,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("note")
    val note: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("phone_code")
    val phoneCode: String,
    @SerializedName("rate")
    val rate: Float,
    @SerializedName("refuse_reason")
    val refuseReason: Any,
    @SerializedName("software_type")
    val softwareType: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("view_count")
    val viewCount: Int,
    @SerializedName("zone")
    val zone: String
)
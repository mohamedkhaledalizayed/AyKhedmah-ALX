package com.mohamed.aykhedmah.data.network.response.settings


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class Data(
    @SerializedName("about_app")
    val aboutApp: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("address1")
    val address1: String,
    @SerializedName("address2")
    val address2: String,
    @SerializedName("advanced_ad")
    val advancedAd: String,
    @SerializedName("android_app")
    val androidApp: String,
    @SerializedName("app_version")
    val appVersion: Int,
    @SerializedName("banner_ad")
    val bannerAd: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("email1")
    val email1: String,
    @SerializedName("email2")
    val email2: Any,
    @SerializedName("facebook")
    val facebook: String,
    @SerializedName("fax")
    val fax: String,
    @SerializedName("google_plus")
    val googlePlus: String,
    @SerializedName("header_logo")
    val headerLogo: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("instagram")
    val instagram: String,
    @SerializedName("ios_app")
    val iosApp: String,
    @SerializedName("latitude")
    val latitude: Int,
    @SerializedName("linkedin")
    val linkedin: String,
    @SerializedName("login_banner")
    val loginBanner: String,
    @SerializedName("longitude")
    val longitude: Int,
    @SerializedName("native_ad")
    val nativeAd: String,
    @SerializedName("phone1")
    val phone1: String,
    @SerializedName("phone2")
    val phone2: String,
    @SerializedName("snapchat_ghost")
    val snapchatGhost: String,
    @SerializedName("telegram")
    val telegram: String,
    @SerializedName("termis_condition")
    val termisCondition: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("title_en")
    val titleEn: String,
    @SerializedName("twitter")
    val twitter: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("whatsapp")
    val whatsapp: String,
    @SerializedName("youtube")
    val youtube: String
)
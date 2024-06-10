package com.mohamed.aykhedmah.data.network.response.notifications


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class Data(
    @SerializedName("action")
    val action: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("from_user_id")
    val fromUserId: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("is_read")
    val isRead: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("message_en")
    val messageEn: Any,
    @SerializedName("notification_date")
    val notificationDate: Int,
    @SerializedName("notification_info_id")
    val notificationInfoId: Int,
    @SerializedName("order_id")
    val orderId: Any,
    @SerializedName("title")
    val title: String,
    @SerializedName("title_en")
    val titleEn: Any,
    @SerializedName("to_user_id")
    val toUserId: Int,
    @SerializedName("updated_at")
    val updatedAt: String
)
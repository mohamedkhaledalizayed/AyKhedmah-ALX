package com.mohamed.aykhedmah.data.network.response.notificationcount


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("count_unread")
    val countUnread: Int
)
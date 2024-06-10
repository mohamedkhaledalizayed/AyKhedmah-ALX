package com.mohamed.aykhedmah.data.network.response.increasecount

import androidx.annotation.Keep

@Keep
data class Details(
    val id: Int,
    val user_id: Int,
    val work_days: String,
    val work_hour: String
)
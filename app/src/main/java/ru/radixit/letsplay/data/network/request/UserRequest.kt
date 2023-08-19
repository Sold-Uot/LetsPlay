package ru.radixit.letsplay.data.network.request

import com.google.gson.annotations.SerializedName

data class ReportUserRequest(
    @SerializedName("text")
    val text: String? = null,

    @SerializedName("cause")
    val cause: Int
)
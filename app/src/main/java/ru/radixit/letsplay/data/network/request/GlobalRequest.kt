package ru.radixit.letsplay.data.network.request

import com.google.gson.annotations.SerializedName

data class ListRequest(

    @SerializedName("userId")
    val userId: String? = "",

    @SerializedName("pageSize")
    val pageSize: String? = null,

    @SerializedName("pageIndex")
    val pageIndex: String? = null,

    @SerializedName("search")
    val search: String? = "",

    @SerializedName("orderBy")
    val orderBy: String? = null
)

data class UploadPhotoChatRequest(
    val base64File: String
)
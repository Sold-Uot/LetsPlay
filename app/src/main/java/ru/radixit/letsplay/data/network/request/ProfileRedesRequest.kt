package ru.radixit.letsplay.data.network.request

import com.google.gson.annotations.SerializedName

data class EditProfileRedesRequest(
    val name: String,
    val surname: String,
    val weight: Int,
    val height: Int,
    val birthday: String,
    val position: Int,
    val gender: Int,
    val city : String,
    val address : String
)

data class ChangePasswordRedesRequest(
    @SerializedName("password")
    val password: String,

    @SerializedName("newPassword")
    val newPassword: String
)
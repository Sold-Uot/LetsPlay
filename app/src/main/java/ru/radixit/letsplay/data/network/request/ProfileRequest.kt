package ru.radixit.letsplay.data.network.request

import com.google.gson.annotations.SerializedName

data class EditProfileRequest(
    val name: String,
    val surname: String,
    val weight: Int,
    val height: Int,
    val birthday: String,
    val position: Int,
    val email: String,
    val gender: Int
)

data class ChangePasswordRequest(
    @SerializedName("password")
    val password: String,

    @SerializedName("newPassword")
    val newPassword: String
)
package ru.radixit.letsplay.data.network.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username")
    var username: String,

    @SerializedName("password")
    var password: String,

    @SerializedName("fcmId")
    val fcmId: String?
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class VerificationRequest(
    val input: String,
    var type: String = "register"
)

data class RegistrationRequest(

    @SerializedName("birthday")
    val birthDate: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("code")
    var code: String = "",

    @SerializedName("fcmId")
    var fcmId: String = "",

    @SerializedName("token")
    var token: String = ""
)

data class ConfirmRequest(
    @SerializedName("code")
    val code: String? = null,

    @SerializedName("token")
    val token: String,

    @SerializedName("resetCode")
    val resetCode: String? = null
)

data class AuthReset(
    @SerializedName("token")
    val token: String,

    @SerializedName("code")
    val code: Int
)

data class AuthResetRequest(

    @SerializedName("password")
    val password: String
)

package ru.radixit.letsplay.data.network.response

import com.google.gson.annotations.SerializedName
import ru.radixit.letsplay.data.model.Debugger
import ru.radixit.letsplay.data.model.Error

data class LoginResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("error")
    val error: String? = null,

    @SerializedName("accessToken")
    var authToken: String,

    @SerializedName("refreshToken")
    var refreshToken: String,

    @SerializedName("expiresIn")
    var timeToken: Int,

    @SerializedName("userId")
    var userId: Int,

    @SerializedName("errors")
    val errors: List<Error>? = null
)

data class VerificationResponse(
    val debugger: Debugger,
    val status: String,
    val token: String,
    val type: String
)

data class RefreshResponse(

    @SerializedName("token")
    val token: String,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("expiresIn")
    val timeToken: Int
)

data class ConfirmResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("token")
    val token: String? = null
)

data class RegistrationResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("errors")
    val errors: List<Error>? = null
)
package ru.radixit.letsplay.domain.repository

import retrofit2.Response
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.LoginResponse
import ru.radixit.letsplay.data.network.response.RefreshResponse
import ru.radixit.letsplay.data.network.response.RegistrationResponse
import ru.radixit.letsplay.data.network.response.VerificationResponse

interface AuthRepository {

    suspend fun loginCheck(request: LoginRequest): Response<LoginResponse>

    suspend fun refresh(refreshToken: RefreshTokenRequest): Response<RefreshResponse>

    suspend fun verify(input: String, type: String): Response<VerificationResponse>

    suspend fun register(registrationRequest: RegistrationRequest): Response<LoginResponse>

    suspend fun reset(authReset: AuthReset): Response<LoginResponse>

    suspend fun password(authResetRequest: AuthResetRequest): Response<RegistrationResponse>

    suspend fun logout(): Response<Error>

}
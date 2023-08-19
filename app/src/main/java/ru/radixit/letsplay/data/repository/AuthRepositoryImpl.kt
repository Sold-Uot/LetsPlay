package ru.radixit.letsplay.data.repository

import retrofit2.Response
import ru.radixit.letsplay.data.network.api.AuthApi
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.LoginResponse
import ru.radixit.letsplay.data.network.response.RefreshResponse
import ru.radixit.letsplay.data.network.response.RegistrationResponse
import ru.radixit.letsplay.data.network.response.VerificationResponse
import ru.radixit.letsplay.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun loginCheck(request: LoginRequest): Response<LoginResponse> {
        return authApi.login(request)
    }

    override suspend fun refresh(refreshToken: RefreshTokenRequest): Response<RefreshResponse> {
        return authApi.refresh(refreshToken)
    }

    override suspend fun verify(input: String, type: String): Response<VerificationResponse> {
        return authApi.verify(VerificationRequest(input, type))
    }

    override suspend fun register(registrationRequest: RegistrationRequest): Response<LoginResponse> {
        return authApi.register(registrationRequest)
    }

    override suspend fun reset(authReset: AuthReset): Response<LoginResponse> {
        return authApi.reset(authReset)
    }

    override suspend fun password(authResetRequest: AuthResetRequest): Response<RegistrationResponse> {
        return authApi.password(authResetRequest)
    }

    override suspend fun logout(): Response<Error> {
        return authApi.logOut()
    }

}
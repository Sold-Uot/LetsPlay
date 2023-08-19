package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.LoginResponse
import ru.radixit.letsplay.data.network.response.RefreshResponse
import ru.radixit.letsplay.data.network.response.RegistrationResponse
import ru.radixit.letsplay.data.network.response.VerificationResponse

interface AuthApi {

    /**
     * Получение access и refresh токенов
     */
    @Headers("isAuthorization: false")
    @POST("/auth/v1/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    /**
     * Сброс access токена
     */
    @Headers("isAuthorization: false")
    @PUT("/auth/v1/refresh")
    suspend fun refresh(@Body refreshToken: RefreshTokenRequest): Response<RefreshResponse>

    /**
     * Отправка проверочного кода
     */
    @Headers("isAuthorization: false")
    @POST("/auth/v1/verification")
    suspend fun verify(@Body request: VerificationRequest): Response<VerificationResponse>

    /**
     * Регистрация
     */
    @Headers("isAuthorization: false")
    @POST("/auth/v1/register")
    suspend fun register(@Body registrationRequest: RegistrationRequest): Response<LoginResponse>

    /**
     * Запрос на смену пароля
     */
    @Headers("isAuthorization: false")
    @PUT("/auth/v1/reset")
    suspend fun reset(@Body authReset: AuthReset): Response<LoginResponse>

    /**
     * Сброс пароля (Доступен после /auth/v1/reset)
     */
    @PUT("/auth/v1/password")
    suspend fun password(@Body authResetRequest: AuthResetRequest): Response<RegistrationResponse>

    /**
     * Выход
     */
    @DELETE("/auth/v1/logout")
    suspend fun logOut(): Response<Error>

}
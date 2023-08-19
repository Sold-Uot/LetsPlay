package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.model.AvatarResponse
import ru.radixit.letsplay.data.network.request.ChangePasswordRequest
import ru.radixit.letsplay.data.network.request.EditProfileRedesRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.*

interface ProfileApi {

    /**
     * Возвращает информацию о текущем профиле
     */
    @GET("/profile/v1/me")
    suspend fun me(): Response<MeResponse>

    /**
     *  Загружает аватарку
     */
    @POST("/profile/v1/avatar")
    suspend fun uploadAvatar(
        @Body request: UploadPhotoChatRequest
    ): Response<AvatarResponse>

    /**
     * Список пользователей, находящихся в черном списке
     */
    @GET("/profile/v1/blackList")
    suspend fun blackList(
        @Query("search") search: String,
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String
    ): Response<ProfileBlackListResponse>

    /**
     * Возвращает информацию о текущем профиле
     */
    @GET("/profile/v1/edit")
    suspend fun fetchProfile(): Response<FetchEditProfileResponse>

    /**
     * Редактирует пользователя
     */
    @PUT("/profile/v1/edit")
    suspend fun editProfile(@Body request: EditProfileRedesRequest): Response<EditProfileResponse>

    /**
     * Изменяет пароль пользователя
     */
    @PUT("/profile/v1/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ReportResponse>

    /**
     * Возвращает настройки уведомления
     */
    @GET("/profile/v1/notification")
    suspend fun fetchNotifications(): Response<NotificationsSettingsResponse>

    /**
     * Настройка уведомления
     */
    @PUT("/profile/v1/notification")
    suspend fun saveNotificationsSettings(@Body request: NotificationsSettingsResponse): Response<ReportResponse>

}
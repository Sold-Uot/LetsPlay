package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import ru.radixit.letsplay.data.model.NewNotifCountModel
import ru.radixit.letsplay.data.network.response.NotificationResponse

interface NotificationApi {

    /**
     * Возвращает список уведомлении
     */
    @GET("/notification/v1/list")
    suspend fun listNotifications(
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String
    ): Response<NotificationResponse>

}
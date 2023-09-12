package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.network.request.ReportUserRequest
import ru.radixit.letsplay.data.network.response.*

interface UserApi {

    /**
     * Поиск пользователей
     */
    @GET("/user/v1/search")
    suspend fun listUsers(
        @Query("search") search: String,
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String
    ): Response<UserResponse>

    /***
     * Данные пользователя
     */
    @GET("/user/v1/get/{ID}")
    suspend fun getProfile(@Path("ID") id: Int): Response<ProfileResponse>

    /**
     * Список событий
     */
    @GET("/user/v1/get/{ID}/events")
    suspend fun eventsListActive(
        @Path("ID") id: Int,
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String,
        @Query("active") flag: Boolean
    ): Response<EventResponse>

    @GET("/user/v1/get/{ID}/events")
    suspend fun eventsListArchive(
        @Path("ID") id: Int,
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String,
        @Query("archive") flag: Boolean
    ): Response<EventResponse>



    /**
     * Список команд
     */
    @GET("/user/v1/get/{ID}/teams")
    suspend fun listTeams(@Path("ID") id: Int): Response<TeamResponse>

    /**
     * Список друзей
     */
    @GET("/user/v1/get/{ID}/friends")
    suspend fun friends(
        @Path("ID") id: Int,
        @Query("search") search: String,
        @Query("pageSize") pageSize: String,
        @Query("pageIndex") pageIndex: String
    ): Response<FriendsResponse>

    /**
     * Добавляет пользователя в черный список
     */
    @POST("/user/v1/get/{ID}/block")
    suspend fun block(@Path("ID") userId: String): Response<UnblockResponse>

    /**
     * Убирает пользователя из черного списка
     */
    @DELETE("/user/v1/get/{ID}/unblock")
    suspend fun unblock(@Path("ID") userId: String): Response<UnblockResponse>

    /**
     * Жалоб на пользователя
     */
    @POST("/user/v1/get/{ID}/report")
    suspend fun report(
        @Path("ID") id: Int,
        @Body request: ReportUserRequest
    ): Response<ReportResponse>

}
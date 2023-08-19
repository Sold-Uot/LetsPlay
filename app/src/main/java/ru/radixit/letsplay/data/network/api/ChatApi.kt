package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.network.request.CreateChatRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.*

interface ChatApi {

    /**
     * Возвращает список чатов
     */
    @GET("/chat/v1/list")
    suspend fun fetchChats(): Response<ChatResponse>

    /**
     * Возвращает историю чата
     */
    @GET("/chat/v1/message/get")
    suspend fun fetchChat(
        @Query("receiverId") receiverId: Int,
        @Query("chatType") chatId: Int
    ): Response<UserChatResponse>

    /**
     * Возвращает информацию чата
     */
    @GET("/chat/v1/getChatInfo")
    suspend fun fetchChatInfo(
        @Query("chatType") chatType: Int,
        @Query("receiverId") receiverId: Int
    ): Response<ChatInfoResponse>

    /**
     * Создает группу
     */
    @POST("/chat/v1/createGroup")
    suspend fun createChat(@Body request: CreateChatRequest): Response<CreateChatResponse>

    /**
     * Загружает фотографию группы
     */
    @POST("/chat/v1/group/{ID}/photo")
    suspend fun uploadGroup(
        @Path("ID") groupId: Int,
        @Body request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse>

}
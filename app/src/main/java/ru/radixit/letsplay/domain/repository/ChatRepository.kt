package ru.radixit.letsplay.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.network.request.CreateChatRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.ChatInfoResponse
import ru.radixit.letsplay.data.network.response.CreateChatResponse
import ru.radixit.letsplay.data.network.response.UploadPhotoChatResponse
import ru.radixit.letsplay.data.network.response.UserChatResponse

interface ChatRepository {

    fun fetchChats(request: ListRequest): Flow<PagingData<Chat>>

    suspend fun fetchChat(receiverId: Int, chatId: Int): Response<UserChatResponse>

    suspend fun fetchChatInfo(chatType: Int, receiverId: Int): Response<ChatInfoResponse>

    suspend fun createGroup(request: CreateChatRequest): Response<CreateChatResponse>

    suspend fun uploadPhoto(
        groupDd: Int,
        request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse>

}
package ru.radixit.letsplay.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.network.api.ChatApi
import ru.radixit.letsplay.data.network.request.CreateChatRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.ChatInfoResponse
import ru.radixit.letsplay.data.network.response.CreateChatResponse
import ru.radixit.letsplay.data.network.response.UploadPhotoChatResponse
import ru.radixit.letsplay.data.network.response.UserChatResponse
import ru.radixit.letsplay.data.paging.ChatPagingSource
import ru.radixit.letsplay.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val service: ChatApi
) : ChatRepository {
    override fun fetchChats(request: ListRequest): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                ChatPagingSource(
                    service = service
                )
            }
        ).flow
    }

    override suspend fun fetchChat(receiverId: Int, chatId: Int): Response<UserChatResponse> {
        return service.fetchChat(receiverId, chatId)
    }

    override suspend fun fetchChatInfo(
        chatType: Int,
        receiverId: Int
    ): Response<ChatInfoResponse> {
        return service.fetchChatInfo(chatType, receiverId)
    }

    override suspend fun createGroup(request: CreateChatRequest): Response<CreateChatResponse> {
        return service.createChat(request)
    }

    override suspend fun uploadPhoto(
        groupId: Int,
        request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse> {
        return service.uploadGroup(groupId, request)
    }
}
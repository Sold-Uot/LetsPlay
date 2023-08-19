package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.model.Photo
import ru.radixit.letsplay.data.model.UserMessage

data class ChatResponse(
    val list: List<Chat>,
    val pages: Int,
    val status: String,
    val total: Int
)

data class UserChatResponse(
    val list: List<UserMessage>,
    val status: String
)

data class ChatInfoResponse(
    val name: String? = null,
    val photo: Photo? = null,
    val status: String,
    val userType: String? = null
)
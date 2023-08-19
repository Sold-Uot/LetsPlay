package ru.radixit.letsplay.data.model

data class ChatMessage(
    val `data`: ChatData,
    val event: String
)

data class ChatData(
    val createdAt: String,
    val id: Int,
    val messageText: String,
    val name: String,
    val userId: Int
)

data class Chat(
    val chatType: Int,
    val id: Int,
    val receiverId: Int,
    val messageText: String? = null,
    val unreadNum: Int? = null,
    val updatedAt: String? = null,
    val name: String? = null,
    val photo: Photo? = null
)

data class UserMessage(
    val chatType: Int? = null,
    val createdAt: String,
    val id: Int? = null,
    val messageText: String? = null,
    val msgType: Int? = null,
    val name: String? = null,
    val receiverId: Int? = null,
    val userId: Int? = null,
    val photo: Photo? = null
)
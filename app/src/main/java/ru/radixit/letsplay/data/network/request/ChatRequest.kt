package ru.radixit.letsplay.data.network.request

import ru.radixit.letsplay.data.model.Member

data class CreateChatRequest(
    val members: List<Member>?,
    val title: String
)
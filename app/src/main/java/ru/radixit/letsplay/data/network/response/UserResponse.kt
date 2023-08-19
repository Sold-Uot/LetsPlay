package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.model.User

data class UserResponse(
    val list: List<User>,
    val pages: Int,
    val status: String,
    val total: Int
)

data class UserArchiveEventResponse(
    val list: List<Event>,
    val pages: Int,
    val status: String,
    val total: Int
)
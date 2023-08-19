package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.User

data class FriendsResponse(
    val list: List<User>,
    val pages: Int,
    val status: String,
    val total: Int
)
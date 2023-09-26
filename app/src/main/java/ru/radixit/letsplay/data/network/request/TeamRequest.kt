package ru.radixit.letsplay.data.network.request

import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.model.MemberList

data class CreateTeamRequest(
    val title: String,
    val nickname: String,
    val members: List<Int>
)

data class DeletePlayerRequest(
    val members : List<Int>
)
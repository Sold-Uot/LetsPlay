package ru.radixit.letsplay.data.network.request

import ru.radixit.letsplay.data.model.Member

data class CreateTeamRequest(
    val title: String,
    val nickname: String,
    val members: List<Int>
)
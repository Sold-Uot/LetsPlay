package ru.radixit.letsplay.data.network.response

import ru.radixit.letsplay.data.model.Error
import ru.radixit.letsplay.data.model.Photo

data class TeamResponse(
    val list: List<Team>,
    val pages: Int,
    val status: String,
    val total: Int
)

data class Team(
    val count: Int? = null,
    val id: Int? = null,
    val my: Boolean,
    val photo: Photo? = null,
    val title: String? = null
)

data class CreateTeamResponse(
    val status: String,
    val message: String,
    val id: Int,
    val errors: List<Error>
)
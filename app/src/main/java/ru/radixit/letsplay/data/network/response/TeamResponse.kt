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

data class ListTeamPlayersResponse(
    val status: String?,
    val total: Int?,
    val pages: Int?,
    val teamForPlayers: TeamForPlayers?,
    val list  : List<UserForTeamPlayers>?

)

data class UserForTeamPlayers(
    val id : Int,
    val name : String,
    val surname :String,
    val username:String,
    val position: String,
    val positionKey : Int,
    val photo: Photo?
)



data class TeamForPlayers(
    val title: String?,
    val count: Int?,
    val photo: Photo?,
    val isBlock : Boolean
)
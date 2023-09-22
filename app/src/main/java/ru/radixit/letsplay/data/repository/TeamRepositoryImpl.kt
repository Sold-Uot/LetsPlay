package ru.radixit.letsplay.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.network.api.TeamApi
import ru.radixit.letsplay.data.network.response.ListTeamPlayersResponse
import ru.radixit.letsplay.data.network.response.ReportResponse
import ru.radixit.letsplay.domain.repository.TeamRepository
import javax.inject.Inject

class TeamRepositoryImpl @Inject constructor(private val teamApi: TeamApi) : TeamRepository {

    override fun fetchListTeamPlayers(id: Int): Flow<Response<ListTeamPlayersResponse>> = flow {
        emit(teamApi.fetchPlayersForTeam(id))
    }

    override fun deletePlayer( id :Int , list :List<Member>) : Flow<Response<ReportResponse>> = flow {

        emit(teamApi.deletePlayer(id, list))
    }

}
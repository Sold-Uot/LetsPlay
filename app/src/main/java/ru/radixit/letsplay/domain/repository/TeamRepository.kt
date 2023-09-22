package ru.radixit.letsplay.domain.repository

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.network.response.ListTeamPlayersResponse
import ru.radixit.letsplay.data.network.response.ReportResponse

interface TeamRepository {

     fun fetchListTeamPlayers(id :Int) : Flow<Response<ListTeamPlayersResponse>>
     fun deletePlayer(id : Int ,list  : List<Member>) : Flow<Response<ReportResponse>>

}
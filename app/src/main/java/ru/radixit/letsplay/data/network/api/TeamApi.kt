package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.network.request.CreateTeamRequest
import ru.radixit.letsplay.data.network.request.DeletePlayerRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.CreateTeamResponse
import ru.radixit.letsplay.data.network.response.ListTeamPlayersResponse
import ru.radixit.letsplay.data.network.response.ReportResponse
import ru.radixit.letsplay.data.network.response.UploadPhotoChatResponse

interface TeamApi {

    /**
     * Создает команду
     */
    @POST("/team/v1/add")
    suspend fun createTeam(@Body request: CreateTeamRequest): Response<CreateTeamResponse>

    /**
     * Возвращает список участников команды
     */
    @POST("/team/v1/get/{ID}/photo")
    suspend fun uploadPhoto(
        @Path("ID") teamId: Int,
        @Body request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse>

    @POST("/team/v1/get/{ID}/accept")
    suspend fun  acceptTeam(@Path("ID") id: Int) : Response<ReportResponse>
    @POST("/team/v1/get/{ID}/reject")
    suspend fun rejectTeam(@Path("ID") id: Int): Response<ReportResponse>

    @GET("/team/v1/get/{ID}/members")
    suspend fun fetchPlayersForTeam(@Path("ID") id : Int) : Response<ListTeamPlayersResponse>

    @HTTP(method = "DELETE", path = "/team/v1/get/{ID}/members_exclude", hasBody = true)
    suspend fun deletePlayer(@Path("ID") id:Int, @Body request: DeletePlayerRequest): Response<ReportResponse>

}
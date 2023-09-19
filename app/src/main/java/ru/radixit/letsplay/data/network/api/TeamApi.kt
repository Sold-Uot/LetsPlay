package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import ru.radixit.letsplay.data.network.request.CreateTeamRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.CreateTeamResponse
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

}
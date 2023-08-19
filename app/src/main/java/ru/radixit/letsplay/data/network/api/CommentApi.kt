package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import ru.radixit.letsplay.data.network.request.ReportUserRequest
import ru.radixit.letsplay.data.network.response.ReportResponse

interface CommentApi {

    /**
     * Добавляет комментарии
     */
    @POST("/comment/v1/add/playground/{ID}")
    suspend fun commentAdd(
        @Path("ID") id: Int,
        @Body request: ReportUserRequest
    ): Response<ReportResponse>

}
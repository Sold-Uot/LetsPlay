package ru.radixit.letsplay.domain.repository

import retrofit2.Response
import ru.radixit.letsplay.data.network.request.ReportUserRequest
import ru.radixit.letsplay.data.network.response.ReportResponse

interface UserRepository {

    suspend fun report(userId: Int, request: ReportUserRequest): Response<ReportResponse>

}
package ru.radixit.letsplay.data.repository

import retrofit2.Response
import ru.radixit.letsplay.data.network.api.UserApi
import ru.radixit.letsplay.data.network.request.ReportUserRequest
import ru.radixit.letsplay.data.network.response.ReportResponse
import ru.radixit.letsplay.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val service: UserApi
) : UserRepository {

    override suspend fun report(userId: Int, request: ReportUserRequest): Response<ReportResponse> {
        return service.report(userId, request)
    }

}
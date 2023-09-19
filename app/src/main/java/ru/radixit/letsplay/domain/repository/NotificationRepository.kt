package ru.radixit.letsplay.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Notification
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.ReportResponse

interface NotificationRepository {

    fun list(request: ListRequest): Flow<PagingData<Notification>>

    suspend fun acceptFriend(userId: String): Response<ReportResponse>


    suspend fun rejectFriend(userId: String): Response<ReportResponse>
    fun acceptEvent(id: Int): Flow<Response<ReportResponse>>

    fun rejectEvent(id: Int): Flow<Response<ReportResponse>>

    fun acceptTeam(id: Int): Flow<Response<ReportResponse>>
    fun rejectTeam(id: Int): Flow<Response<ReportResponse>>


}
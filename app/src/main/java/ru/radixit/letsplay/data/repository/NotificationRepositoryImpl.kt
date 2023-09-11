package ru.radixit.letsplay.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import ru.radixit.letsplay.data.model.Notification
import ru.radixit.letsplay.data.network.api.EventApi
import ru.radixit.letsplay.data.network.api.FriendApi
import ru.radixit.letsplay.data.network.api.NotificationApi
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.ReportResponse
import ru.radixit.letsplay.data.paging.NotificationPagingSource
import ru.radixit.letsplay.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val service: NotificationApi,
    private val eventApi: EventApi ,
    private val friendApi: FriendApi
) : NotificationRepository {

    override fun list(request: ListRequest): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                NotificationPagingSource(service = service)
            }
        ).flow
    }

    override suspend fun acceptFriend(userId: String): Response<ReportResponse> {
        return friendApi.addToFriends(userId)
    }

    override suspend fun rejectFriend(userId: String): Response<ReportResponse> {
        return friendApi.deleteFromFriend(userId.toInt())
    }

    override fun acceptEvent(id: Int): Flow<Response<ReportResponse>> = flow {

        emit(eventApi.acceptEvent(id))
    }.flowOn(Dispatchers.IO)

    override fun rejectEvent(id: Int): Flow<Response<ReportResponse>> = flow {

        emit(eventApi.rejectEvent(id))
    }.flowOn(Dispatchers.IO)

}
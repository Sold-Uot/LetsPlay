package ru.radixit.letsplay.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.network.api.EventApi
import ru.radixit.letsplay.data.network.api.ProfileApi
import ru.radixit.letsplay.data.network.request.JoinEventRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.EventForMap
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.data.network.response.JoinEventResponse
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.data.network.response.NewEventDescriptionResponse
import ru.radixit.letsplay.data.paging.GlobalEventPagingSource
import ru.radixit.letsplay.domain.repository.EventRepository
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val service: EventApi,
    private val profileApi: ProfileApi
) : EventRepository {

    override fun listEvents(request: ListRequest): Flow<PagingData<Event>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                GlobalEventPagingSource(
                    query = request.search.toString(),
                    service = service
                )
            }
        ).flow
    }


    override suspend fun listEventsMembers(id: String): Response<EventMembersResp> {
        return service.listEventsMembers(id)
    }

    override fun getEvent(id: String): Flow<Response<NewEventDescriptionResponse>> = flow<Response<NewEventDescriptionResponse>> {

        emit(service.getEvent(id = id))
    }.flowOn(Dispatchers.IO)



    override suspend fun getEventForMap(id: String): Response<EventForMap> {
        return service.getEventForMap(id.toInt())
    }

    override suspend fun maps(): Response<MapsResponse> {
        return service.getMaps()
    }

    override suspend fun joinEvent(
        eventId: String,
        request: JoinEventRequest
    ): Response<JoinEventResponse> {
        return service.joinEvent(eventId, request)
    }

}
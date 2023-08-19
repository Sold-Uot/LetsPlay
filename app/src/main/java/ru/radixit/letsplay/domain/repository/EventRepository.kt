package ru.radixit.letsplay.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.network.request.JoinEventRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.EventDescriptionResponse
import ru.radixit.letsplay.data.network.response.EventForMap
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.data.network.response.JoinEventResponse
import ru.radixit.letsplay.data.network.response.MapsResponse

interface EventRepository {

    fun listEvents(request: ListRequest): Flow<PagingData<Event>>

    suspend fun getEvent(id: String): Response<EventDescriptionResponse>
    suspend fun listEventsMembers(id: String): Response<EventMembersResp>

    suspend fun maps(): Response<MapsResponse>

    suspend fun getEventForMap(id: String): Response<EventForMap>

    suspend fun joinEvent(eventId: String, request: JoinEventRequest): Response<JoinEventResponse>

}
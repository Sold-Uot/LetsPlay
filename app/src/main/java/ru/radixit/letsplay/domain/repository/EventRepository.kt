package ru.radixit.letsplay.domain.repository


import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.data.network.request.CreateEventRequest
import ru.radixit.letsplay.data.network.request.JoinEventRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.CreateEventResponse
import ru.radixit.letsplay.data.network.response.EventForMap
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.data.network.response.JoinEventResponse
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.data.network.response.NewEventDescriptionResponse
import ru.radixit.letsplay.data.network.response.PhotoResponse
import ru.radixit.letsplay.utils.LoadState

interface EventRepository {

    fun  addFriendToInviteList(user : FriendEntity) : Flow<LoadState<FriendEntity>>

    fun removeFriendToInviteList(user : FriendEntity) : Flow<LoadState<FriendEntity>>

    fun fetchFriendToInviteList() : Flow<LoadState<List<FriendEntity>>>

    fun getCountFriendInDB():Flow<Int>

    fun listEvents(request: ListRequest): Flow<PagingData<Event>>

    suspend fun creatEvent(request: CreateEventRequest):Response<CreateEventResponse>

    suspend fun upload(id: String, base64File: String):Response<PhotoResponse>

    suspend fun listEventsMembers(id: String): Response<EventMembersResp>

    fun getEvent(id :String) : Flow<Response<NewEventDescriptionResponse>>
    suspend fun maps(): Response<MapsResponse>

    suspend fun getEventForMap(id: String): Response<EventForMap>

    suspend fun joinEvent(eventId: String, request: JoinEventRequest): Response<JoinEventResponse>

}
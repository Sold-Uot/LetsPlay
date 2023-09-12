package ru.radixit.letsplay.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.NewNotifCountModel
import ru.radixit.letsplay.data.model.Playground
import ru.radixit.letsplay.data.model.PlaygroundForMap
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.*

interface PlaygroundRepository {
    fun getListPlaygrounds(listRequest: ListRequest): Flow<PagingData<Playground>>

    suspend fun maps(): Response<MapsResponse>
    suspend fun newNotifCount(): Response<NewNotifCountModel>

    suspend fun getPlayground(id: String): Response<PlaygroundInDetail>

    suspend fun comment(id: String, request: ReportUserRequest): Response<ReportResponse>

    suspend fun addPlayground(request: CreatePlaygroundRequest): Response<CreatePlaygroundResponse>

    suspend fun upload(id: String, base64File: String): Response<PhotoResponse>

    suspend fun addresses(request: DaDataRequest): Response<DaDataResponse>

//    suspend fun commentList(request: EventRequest): Response<>



    suspend fun fetchHoursFree(id: String, weekDay: String): Response<HoursFreeResponse>

    suspend fun fetchDisabledDays(id: Int, month: String, year: String): Response<CalendarDays>

    suspend fun fetchPhotos(id: Int): Response<PlaygroundsPhotosResponse>

    suspend fun getPlaygroundForMap(id: Int): Response<PlaygroundForMap>

    suspend fun calendarHours(id: Int, date: String): Response<FreeHoursResponse>

    suspend fun fetchComments(id: Int): Response<CommentResponse>
}
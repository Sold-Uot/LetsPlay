package ru.radixit.letsplay.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.*
import ru.radixit.letsplay.data.model.Playground
import ru.radixit.letsplay.data.network.RetrofitInstance
import ru.radixit.letsplay.data.network.api.CommentApi
import ru.radixit.letsplay.data.network.api.EventApi
import ru.radixit.letsplay.data.network.api.PlaygroundsApi
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.*
import ru.radixit.letsplay.data.paging.PlaygroundPagingSource
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaygroundRepositoryImpl @Inject constructor(
    private val playgroundsApi: PlaygroundsApi,
    private val retrofitInstance: RetrofitInstance,
    private val eventApi: EventApi,
    private val commentApi: CommentApi,
) : PlaygroundRepository {

    override fun getListPlaygrounds(request: ListRequest): Flow<PagingData<Playground>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                PlaygroundPagingSource(
                    query = request.search.toString(),
                    service = playgroundsApi
                )
            }
        ).flow
        return pager
    }

    override suspend fun maps(): Response<MapsResponse> {
        return playgroundsApi.maps()
    }
    override suspend fun newNotifCount(): Response<NewNotifCountModel> {
        return playgroundsApi.newNotifCount()
    }

    override suspend fun getPlayground(id: String): Response<PlaygroundInDetail> {
        return playgroundsApi.getPlayground(id)
    }

    override suspend fun fetchPhotos(id: Int): Response<PlaygroundsPhotosResponse> {
        return playgroundsApi.fetchPhotos(id)
    }

    override suspend fun getPlaygroundForMap(id: Int): Response<PlaygroundForMap> {
        return playgroundsApi.getPlaygroundForMap(id)
    }

    override suspend fun calendarHours(id: Int, date: String): Response<FreeHoursResponse> {
        return playgroundsApi.calendarHours(id, date)
    }

    override suspend fun fetchComments(id: Int): Response<CommentResponse> {
        return playgroundsApi.fetchComments(id)
    }

    override suspend fun comment(id: String, request: ReportUserRequest): Response<ReportResponse> {
        return commentApi.commentAdd(id.toInt(), request)
    }

    override suspend fun addPlayground(request: CreatePlaygroundRequest): Response<CreatePlaygroundResponse> {
        return playgroundsApi.addPlayground(request)
    }

    override suspend fun upload(id: String, base64File: String): Response<PhotoResponse> {
        return playgroundsApi.upload(id, UploadPhoto(base64File))
    }

    override suspend fun addresses(request: DaDataRequest): Response<DaDataResponse> {
        return retrofitInstance.api.addresses(request)
    }

    override suspend fun createEvent(request: CreateEventRequest): Response<CreateEventResponse> {
        return eventApi.createEvent(request)
    }

    override suspend fun fetchHoursFree(id: String, weekDay: String): Response<HoursFreeResponse> {
        return playgroundsApi.fetchHoursFree(id, weekDay)
    }

    override suspend fun fetchDisabledDays(
        id: Int,
        month: String,
        year: String
    ): Response<CalendarDays> {
        return playgroundsApi.calendarDays(id, month, year)
    }

}
package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.network.request.CreateEventRequest
import ru.radixit.letsplay.data.network.request.JoinEventRequest
import ru.radixit.letsplay.data.network.response.*

interface EventApi {

    /**
     * Возвращает список событии
     */
    @GET("/event/v1/list")
    suspend fun listEvents(
        @Query("pageIndex") pageIndex: String?,
        @Query("search") search: String?,
        @Query("pageSize") pageSize: String?
    ): Response<EventResponse>

    @GET("/event/v1/get/{ID}/members")
    suspend fun listEventsMembers(@Path("ID") id: String): Response<EventMembersResp>


    @GET("/event/v1/get/{ID}")
    suspend fun getEvent(@Path("ID") id: String): Response<NewEventDescriptionResponse>


    @POST("/event/v1/get/{ID}/accept")
    suspend fun acceptEvent(@Path("ID") id: Int): Response<ReportResponse>

    @POST("/event/v1/get/{ID}/reject")
    suspend fun rejectEvent(@Path("ID") id: Int): Response<ReportResponse>

    /**
     * Возвращает список событии на карте
     */
    @GET("/event/v1/maps")
    suspend fun getMaps(): Response<MapsResponse>

    /**
     * Возвращает данные событии
     */
    @GET("/event/v1/maps/get/{ID}")
    suspend fun getEventForMap(@Path("ID") id: Int): Response<EventForMap>

    /**
     * Создает событии
     */
    @POST("/event/v1/add")
    suspend fun createEvent(@Body request: CreateEventRequest): Response<CreateEventResponse>

    /**
     * Отправляем заявку на участие
     */
    @POST("/event/v1/get/{eventId}/join")
    suspend fun joinEvent(
        @Path("eventId") eventId: String,
        @Body request: JoinEventRequest
    ): Response<JoinEventResponse>

}
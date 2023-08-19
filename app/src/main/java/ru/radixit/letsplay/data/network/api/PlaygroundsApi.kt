package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.*
import ru.radixit.letsplay.data.model.NewNotifCountModel
import ru.radixit.letsplay.data.model.PlaygroundForMap
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.data.model.UploadPhoto
import ru.radixit.letsplay.data.network.request.CreatePlaygroundRequest
import ru.radixit.letsplay.data.network.response.*

interface PlaygroundsApi {

    /**
     * Возвращает список площадок
     */
    @GET("/playground/v1/list")
    suspend fun listPlaygrounds(
        @Query("search") search: String?,
        @Query("pageIndex") pageIndex: String?,
        @Query("pageSize") pageSize: String?
    ): Response<PlaygroundResponse>

    /**
     * Возвращает список площадок на карте
     */
    @GET("/playground/v1/maps")
    suspend fun maps(): Response<MapsResponse>

    /**
     * Возвращает кол-во новых уведомлений
     */
    @GET("/notification/v1/total_unreaded")
    suspend fun newNotifCount(
    ): Response<NewNotifCountModel>
    @GET("/playground/v1/get/{ID}")
    suspend fun getPlayground(@Path("ID") id: String): Response<PlaygroundInDetail>

    /**
     * Возвращает данные площадки для карты
     */
    @GET("/playground/v1/maps/get/{ID}")
    suspend fun getPlaygroundForMap(@Path("ID") id: Int): Response<PlaygroundForMap>

    /**
     *  Загрузка фотографий
     */
    @POST("/playground/v1/get/{ID}/photo")
    suspend fun upload(
        @Path("ID") id: String,
        @Body uploadPhoto: UploadPhoto
    ): Response<PhotoResponse>

    /**
     * Создает площадку
     */
    @POST("/playground/v1/add")
    suspend fun addPlayground(@Body request: CreatePlaygroundRequest): Response<CreatePlaygroundResponse>

    @POST("/schedule/v1/getHoursFree")
    @FormUrlEncoded
    suspend fun fetchHoursFree(
        @Field("id") id: String,
        @Field("weekDay") weekDay: String
    ): Response<HoursFreeResponse>

    /**
     * Возвращает список фотографии
     */
    @GET("/playground/v1/get/{ID}/photos")
    suspend fun fetchPhotos(@Path("ID") id: Int): Response<PlaygroundsPhotosResponse>

    /**
     * Получить часы для календаря
     */
    @GET("/playground/v1/get/{ID}/calendarHours")
    suspend fun calendarHours(
        @Path("ID") id: Int,
        @Query("date") date: String
    ): Response<FreeHoursResponse>

    @GET("/playground/v1/get/{ID}/calendarDays")
    suspend fun calendarDays(
        @Path("ID") id: Int,
        @Query("month") month: String,
        @Query("year") year: String
    ): Response<CalendarDays>

    /**
     * Список комментарии
     */
    @GET("/playground/v1/get/{ID}/comments")
    suspend fun fetchComments(@Path("ID") id: Int): Response<CommentResponse>

}
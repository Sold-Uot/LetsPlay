package ru.radixit.letsplay.data.network.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import ru.radixit.letsplay.data.network.response.ReportResponse

interface FriendApi {

    /**
     * Одобряет или создает заявку на добавление в друзья
     */
    @POST("/friend/v1/get/{ID}/add")
    suspend fun addToFriends(@Path("ID") userId: String): Response<ReportResponse>

    /**
     * Удаляет пользователя из списка друзей или отклоняет заявку в друзья
     */
    @DELETE("/friend/v1/get/{ID}")
    suspend fun deleteFromFriend(@Path("ID") id: Int): Response<ReportResponse>

}
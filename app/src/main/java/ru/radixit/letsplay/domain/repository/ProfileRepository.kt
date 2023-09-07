package ru.radixit.letsplay.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import ru.radixit.letsplay.data.model.AvatarResponse
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.*

interface ProfileRepository {

    suspend fun uploadAvatar(
        base64File: String
    ): Response<AvatarResponse>

    suspend fun eventListFlow( id : Int):Response<EventResponse>
    suspend fun getProfile(id: Int): Response<ProfileResponse>

    fun getProfileData(id: Int): Flow<Response<ProfileResponse>>

    suspend fun eventsList(
        request: Int,
        pageSize: String,
        pageIndex: String,
        filter: String
    ): Response<EventResponse>

    fun blackList(request: ListRequest): Flow<PagingData<User>>

    suspend fun listTeams(request: Int): Response<TeamResponse>


    suspend fun editProfile(request: EditProfileRedesRequest): Response<EditProfileResponse>

    suspend fun fetchProfile(): Response<FetchEditProfileResponse>

    fun friends(request: ListRequest): Flow<PagingData<User>>


    fun findFriend(request: ListRequest): Flow<PagingData<User>>

    suspend fun unblock(userId: String): Response<UnblockResponse>

    suspend fun getUserProfile(userId: String): Response<ProfileResponse>

    suspend fun block(userId: String): Response<UnblockResponse>

    suspend fun addToFriends(userId: String): Response<ReportResponse>

    suspend fun createTeam(request: CreateTeamRequest): Response<CreateTeamResponse>

    suspend fun uploadPhoto(
        teamId: Int,
        request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse>

    suspend fun changePassword(request: ChangePasswordRequest): Response<ReportResponse>

    suspend fun fetchNotifications(): Response<NotificationsSettingsResponse>

    suspend fun deleteFromFriend(id: Int): Response<ReportResponse>

    suspend fun saveNotifications(request: NotificationsSettingsResponse): Response<ReportResponse>
}
package ru.radixit.letsplay.data.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.radixit.letsplay.data.model.AvatarResponse
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.api.FriendApi
import ru.radixit.letsplay.data.network.api.ProfileApi
import ru.radixit.letsplay.data.network.api.TeamApi
import ru.radixit.letsplay.data.network.api.UserApi
import ru.radixit.letsplay.data.network.request.*
import ru.radixit.letsplay.data.network.response.*
import ru.radixit.letsplay.data.paging.BlackListPagingSource
import ru.radixit.letsplay.data.paging.FindFriendPagingSource
import ru.radixit.letsplay.data.paging.FriendsPagingSource
import ru.radixit.letsplay.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val service: ProfileApi,
    private val userApi: UserApi,
    private val friendApi: FriendApi,
    private val teamApi: TeamApi
) : ProfileRepository {

    override suspend fun getProfile(id: Int): Response<ProfileResponse> {
        return userApi.getProfile(id)
    }

    override suspend fun uploadAvatar(
        base64File: String
    ): Response<AvatarResponse> {
        return service.uploadAvatar(UploadPhotoChatRequest(base64File))
    }

    override suspend fun eventsList(
        request: Int,
        pageSize: String,
        pageIndex: String,
        filter: String
    ): Response<EventResponse> {
        return userApi.eventsList(
            request,
            pageSize = pageSize,
            pageIndex = pageIndex,
            filter = filter
        )
    }

    override fun blackList(request: ListRequest): Flow<PagingData<User>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                BlackListPagingSource(
                    query = request.search.toString(),
                    service = service
                )
            }
        ).flow
    }

    override suspend fun listTeams(request: Int): Response<TeamResponse> {
        return userApi.listTeams(request)
    }

    override suspend fun addToFriends(userId: String): Response<ReportResponse> {
        return friendApi.addToFriends(userId)
    }

    override suspend fun createTeam(request: CreateTeamRequest): Response<CreateTeamResponse> {
        return teamApi.createTeam(request)
    }

    override suspend fun uploadPhoto(
        teamId: Int,
        request: UploadPhotoChatRequest
    ): Response<UploadPhotoChatResponse> {
        return teamApi.uploadPhoto(teamId, request)
    }

    override suspend fun editProfile(request: EditProfileRedesRequest): Response<EditProfileResponse> {
        return service.editProfile(request)
    }

    override suspend fun fetchProfile(): Response<FetchEditProfileResponse> {
        return service.fetchProfile()
    }

    override fun friends(request: ListRequest): Flow<PagingData<User>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FriendsPagingSource(
                    id = request.userId!!.toInt(),
                    query = request.search.toString(),
                    service = userApi,
                )
            }
        ).flow
    }

    override fun findFriend(request: ListRequest): Flow<PagingData<User>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                FindFriendPagingSource(
                    query = request.search.toString(),
                    service = userApi
                )
            }
        ).flow.map {
            val userList = mutableSetOf<Int>()
            it.filter { person ->
                if (userList.contains(person.id)) {
                    false
                } else {
                    userList.add(person.id)
                }
            }
        }
    }

    override suspend fun unblock(userId: String): Response<UnblockResponse> {
        return userApi.unblock(userId)
    }

    override suspend fun getUserProfile(userId: String): Response<ProfileResponse> {
        return userApi.getProfile(userId.toInt())
    }

    override suspend fun block(userId: String): Response<UnblockResponse> {
        return userApi.block(userId)
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Response<ReportResponse> {
        return service.changePassword(request)
    }

    override suspend fun fetchNotifications(): Response<NotificationsSettingsResponse> {
        return service.fetchNotifications()
    }

    override suspend fun deleteFromFriend(id: Int): Response<ReportResponse> {
        return friendApi.deleteFromFriend(id)
    }

    override suspend fun saveNotifications(request: NotificationsSettingsResponse): Response<ReportResponse> {
        return service.saveNotificationsSettings(request)
    }

}
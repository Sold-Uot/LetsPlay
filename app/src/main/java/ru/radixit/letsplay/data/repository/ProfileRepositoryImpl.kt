package ru.radixit.letsplay.data.repository

import android.util.Log
import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import ru.radixit.letsplay.data.database.UserDao
import ru.radixit.letsplay.data.model.AvatarResponse
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.model.UserEntity
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
import ru.radixit.letsplay.utils.LoadState
import java.lang.Exception
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val service: ProfileApi,
    private val userApi: UserApi,
    private val friendApi: FriendApi,
    private val profileApi: ProfileApi,
    private val teamApi: TeamApi,
    private val userDao: UserDao
) : ProfileRepository {

    override suspend fun getProfile(id: Int): Response<ProfileResponse> {
        return userApi.getProfile(id)
    }


    override fun getProfileData(id: Int): Flow<Response<ProfileResponse>> = flow {
        emit(userApi.getProfile(id))
    }.flowOn(Dispatchers.IO)


    override suspend fun uploadAvatar(
        base64File: String
    ): Response<AvatarResponse> {
        return service.uploadAvatar(UploadPhotoChatRequest(base64File))
    }

    override suspend fun eventListFlow(id: Int): Response<EventResponse> {
        return userApi.eventsListArchive(id, pageSize = "30", pageIndex = "1", flag = true)
    }

    override suspend fun eventsList(
        request: Int,
        pageSize: String,
        pageIndex: String,
        filter: String
    ): Response<EventResponse> {
        return userApi.eventsListActive(
            request,
            pageSize = pageSize,
            pageIndex = pageIndex,
            flag = true
        )
    }

    override suspend fun eventsListArchive(
        request: Int,
        pageSize: String,
        pageIndex: String,
        flag: Boolean
    ): Response<EventResponse> {
        return userApi.eventsListArchive(
            request,
            pageSize = pageSize,
            pageIndex = pageIndex,
            flag = true
        )
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



    override fun getAllUserList(): Flow<List<UserEntity>> = flow {
        Log.e("addsus","12")

        emit(userDao.getALlUser())
    }

    override fun addUser(user: UserEntity): Flow<LoadState<UserEntity>> = flow  {
        emit(LoadState.loading())
        try {
            userDao.addUser(user)
            Log.e("add",userDao.getALlUser().toString())
            emit(LoadState.success(user))
        }
        catch (ex: Exception){
            emit(LoadState.error(ex.toString()))
            Log.e("ex "  , ex.message.toString())
        }


    }

    override fun removeUser(user: UserEntity): Flow<LoadState<UserEntity>> = flow  {
        emit(LoadState.loading())
        try {
            userDao.removeUser(user)
            emit(LoadState.success(user))
        }
        catch (ex: Exception){
            emit(LoadState.error(ex.toString()))
        }
    }

    override suspend fun addSuspend(user: UserEntity) {
        Log.e("addsus","12")

        userDao.addUser(user)
    }
}
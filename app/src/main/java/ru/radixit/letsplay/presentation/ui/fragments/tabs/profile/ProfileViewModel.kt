package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.model.Gender
import ru.radixit.letsplay.data.model.Photo
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.api.UserApi
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.MeResponse
import ru.radixit.letsplay.data.network.response.ProfileResponse
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.data.paging.ArchiveEventPagingSource
import ru.radixit.letsplay.data.paging.EventPagingSource
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    private val userApi: UserApi,
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _successAddToFriends = MutableLiveData<Boolean>()
    val successAddToFriends: LiveData<Boolean> = _successAddToFriends
    private val _gender = MutableLiveData<Gender?>()
    val gender: LiveData<Gender?> = _gender

    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile

    private val _profile_player = MutableLiveData<ProfileResponse>()
    val profile_player: LiveData<ProfileResponse> = _profile_player

    private val _id_profile = MutableLiveData<Int>()

    private val _photo = MutableLiveData<Photo?>()
    val photo: LiveData<Photo?> = _photo

    private val _request = MutableLiveData<Int>()
    val request : LiveData<Int> = _request

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

    private val _event = MutableLiveData<List<Event>>()
    val eventLiveData : LiveData <List<Event>> = _event

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading



    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response




    fun checkThisMyProfile(id :Int):Boolean {

        if (_id_profile.value == id){
            return false
        }
        return true
    }


    fun searchUsers(query: String = "", userId: String): Flow<PagingData<User>> {
        return repository.friends(ListRequest(search = query, userId = userId))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)

    }

    fun block(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.block(userId)
                if (response.isSuccessful) {
                    _response.value = response.body()?.message
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun getProfilePlayerData(id: Int){
        viewModelScope.launch {
            try {
                getRequest(id)
                _loading.value = true
                val response = repository.getProfileData(id)
                response.collect{
                    Log.e("123",it.body()?.id.toString())
                    _profile_player.value = it.body()
                    /*_gender.value = it.body()?.gender
                    _photo.value = it.body()?.photo*/


                }
            }


            catch (ex:Exception) {
                errorHandler.proceed(ex) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                _loading.value = false
            }


        }
    }
    fun getProfileData(id: Int){
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getUserProfile(id.toString())

                        Log.e("акrrкаунт" , response.body()!!.id.toString())
                        _id_profile.value = response.body()!!.id
                        _profile.value = response.body()
                        _gender.value = response.body()?.gender
                        _photo.value = response.body()?.photo



                }


            catch (ex:Exception) {
                errorHandler.proceed(ex) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
            finally {
                _loading.value = false
            }


        }}


    fun getRequest(id : Int){
        Log.e("request_id " , id.toString())
        _request.value = id
    }
    // старый способо тянуть данные профиля

    fun fetchProfile(id: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getProfile(id)
                if (response.isSuccessful) {
                    _profile.value = response.body()
                    _gender.value = response.body()?.gender
                    _photo.value = response.body()?.photo
                }

            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                _loading.value = false
            }

        }
    }

    fun addToFriends(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.addToFriends(userId)
                if (response.isSuccessful) {
                    response.body()?.let { context.showToast(it.message) }
                    _successAddToFriends.value = true
                } else {
                    _successAddToFriends.value = false
                }
            } catch (e: java.lang.Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }


    val events: Flow<PagingData<Event>> = Pager(PagingConfig(pageSize = 1)) {
        EventPagingSource(repository, sessionManager.fetchToken())
    }.flow
        .cachedIn(viewModelScope)




        .cachedIn(viewModelScope)
    val archiveEvents: Flow<PagingData<Event>> = Pager(PagingConfig(pageSize = 1)) {
        ArchiveEventPagingSource(repository, sessionManager.fetchToken())
    }.flow
        .cachedIn(viewModelScope)
    fun events(request: Int): Flow<PagingData<Event>> {
        return Pager(PagingConfig(pageSize = 1)) {
            EventPagingSource(repository, request)
        }.flow
            .cachedIn(viewModelScope)
    }

    fun archiveEvents(request: Int):  Flow<PagingData<Event>> = Pager(PagingConfig(pageSize = 1)) {
        ArchiveEventPagingSource(repository, request)
    }.flow
        .cachedIn(viewModelScope)

    fun listEvent(id : Int) {
        viewModelScope.launch {

                val res = repository.eventListFlow(id)
                if (res.isSuccessful) {
                    _event.value = res.body()!!.list
                }



        }
    }
    fun listTeams(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.listTeams(id)
                if (response.isSuccessful) {
                    _teams.value = response.body()?.list
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}
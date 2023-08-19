package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.Gender
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.ProfileResponse
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _gender = MutableLiveData<Gender>()
    val gender: LiveData<Gender> = _gender
    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile
    private val _photo = MutableLiveData<String>()
    val photo: LiveData<String> = _photo
    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams
    var vUserId: String? = null
    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response
    private val _successAddToFriends = MutableLiveData<Boolean>()
    val successAddToFriends: LiveData<Boolean> = _successAddToFriends
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun searchUsers(query: String = "", userId: String): Flow<PagingData<User>> {
        return repository.friends(ListRequest(search = query, userId = userId))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }
    fun searchFriends(query: String = ""): Flow<PagingData<User>> {
        return repository.friends(
            ListRequest(
                search = query,
                userId = sessionManager.fetchToken().toString()
            )
        )
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
            .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(), replay = 10)
    }
    fun fetchProfile(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                vUserId = userId
                val response = repository.getUserProfile(userId)
                if (response.isSuccessful) {
                    _profile.value = response.body()
                    _gender.value = response.body()?.gender!!
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
}
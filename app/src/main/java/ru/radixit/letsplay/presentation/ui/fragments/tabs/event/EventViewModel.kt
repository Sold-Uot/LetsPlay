package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.network.request.JoinEventRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.ProfileResponse
import ru.radixit.letsplay.data.paging.EventPagingSource
import ru.radixit.letsplay.domain.global.ResourceManager
import ru.radixit.letsplay.domain.repository.EventRepository
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val resourceManager: ResourceManager,
    private val repository: EventRepository,
    private val profileRep: ProfileRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile

    private val _position = MutableLiveData(0)
    val position: LiveData<Int> = _position

    private val _textPosition = MutableLiveData<String>()
    val textPosition: LiveData<String> = _textPosition

    private val _textMessage = MutableLiveData<String>()
    val textMessage: LiveData<String> = _textMessage

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success

    fun searchEvents(query: String = ""): Flow<PagingData<Event>> {
        return repository.listEvents(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }

    fun events(request: Int): Flow<PagingData<Event>> {
        return Pager(PagingConfig(pageSize = 1)) {
            EventPagingSource(profileRep, request)
        }.flow
            .cachedIn(viewModelScope)
    }
    fun fetchProfile(id: Int) {
        viewModelScope.launch {
            try {
                val response = profileRep.getProfile(id)
                if (response.isSuccessful) {
                    _profile.value = response.body()
                }

            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
//                _loading.value = false
            }

        }
    }

    fun changePositionOnField() {
        when (_position.value) {
            3 -> {
                _textPosition.value =
                    resourceManager.getString(R.string.edit_profile_position_defender)
                _position.value = 2
            }
            1 -> {
                _textPosition.value =
                    resourceManager.getString(R.string.edit_profile_position_goalkeeper)
                _position.value = 3
            }
            else -> {
                _textPosition.value = resourceManager.getString(R.string.edit_profile_position)
                _position.value = 1
            }
        }
    }

    fun join(id: String, position: Int) {
        try {
            viewModelScope.launch {
                val response = repository.joinEvent(id, JoinEventRequest(position = position))
                if (response.isSuccessful && response.body()?.status == "ok") {
                    _success.value = true
                    _textMessage.value = response.body()?.message
                } else {
                    _textMessage.value = "Что-то пошло не так"
                    _success.value = false
                }
            }
        } catch (e: Exception) {
            _success.value = false
            errorHandler.proceed(e) { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
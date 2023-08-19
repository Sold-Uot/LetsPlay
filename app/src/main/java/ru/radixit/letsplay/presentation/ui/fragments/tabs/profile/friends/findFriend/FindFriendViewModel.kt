package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.findFriend

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import javax.inject.Inject


@HiltViewModel
class FindFriendViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response

    fun searchUsers(query: String): Flow<PagingData<User>> {
        return repository.findFriend(ListRequest(search = query))
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
}
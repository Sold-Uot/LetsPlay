package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.blackList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.domain.repository.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class BlackListViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _successUnblock = MutableLiveData<Boolean>()
    val successUnblock: LiveData<Boolean> = _successUnblock

    fun searchUsers(query: String = ""): Flow<PagingData<User>> {
        return repository.blackList(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }

    fun unblock(userId: String) {
        viewModelScope.launch {
            val response = repository.unblock(userId)
            _successUnblock.value = response.isSuccessful
        }
    }
}
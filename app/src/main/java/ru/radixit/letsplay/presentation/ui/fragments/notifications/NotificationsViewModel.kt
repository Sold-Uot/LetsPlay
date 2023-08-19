package ru.radixit.letsplay.presentation.ui.fragments.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.Notification
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * VM для уведомление
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    fun searchNotifications(): Flow<PagingData<Notification>> {
        return repository.list(ListRequest())
            .map { pagingData -> pagingData.map { it } }
            .cachedIn(viewModelScope)
    }

    fun acceptFriend(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.acceptFriend(userId)
                if (response.isSuccessful) {
                }
            } catch (e: java.lang.Exception) {

            }
        }
    }

    fun rejectFriend(userId: String) {
        viewModelScope.launch {
            try {
                val response = repository.rejectFriend(userId)
                if (response.isSuccessful) {
                }
            } catch (e: java.lang.Exception) {

            }
        }
    }

}
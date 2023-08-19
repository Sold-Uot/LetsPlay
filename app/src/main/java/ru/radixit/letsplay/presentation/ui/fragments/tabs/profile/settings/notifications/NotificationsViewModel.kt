package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.notifications

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.network.response.NotificationsSettingsResponse
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject


@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _invite = MutableLiveData<Boolean>()
    val invite: LiveData<Boolean> get() = _invite
    private val _events = MutableLiveData<Boolean>()
    val events: LiveData<Boolean> get() = _events
    private val _friends = MutableLiveData<Boolean>()
    val friends: LiveData<Boolean> get() = _friends
    private val _messages = MutableLiveData<Boolean>()
    val messages: LiveData<Boolean> get() = _messages

    fun fetchNotificationsSettings() {
        viewModelScope.launch {
            try {
                val response = repository.fetchNotifications()
                if (response.isSuccessful) {
                    _invite.value = response.body()?.noticeInvitation
                    _events.value = response.body()?.noticeEvent
                    _friends.value = response.body()?.noticeFriend
                    _messages.value = response.body()?.noticeMessage
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun saveNotificationsSettings(request: NotificationsSettingsResponse,result: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.saveNotifications(request)
                if (response.isSuccessful) {
                    context.showToast("Изменения успешно сохранены")
                    result(true)
                }else{
                    result(false)
                }
            } catch (e: Exception) {
                result(false)
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }
}
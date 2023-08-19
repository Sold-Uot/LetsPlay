package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class RemoveFromFriendsViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun removeFromFriends(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteFromFriend(id)
                _success.value = response.isSuccessful
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

}
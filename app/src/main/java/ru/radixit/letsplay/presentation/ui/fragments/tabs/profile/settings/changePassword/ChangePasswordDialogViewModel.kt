package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.changePassword

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.network.request.ChangePasswordRequest
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class ChangePasswordDialogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler,
    private val repository: ProfileRepository
) : ViewModel() {

    private val _oldAndNewPasswordCompare = MutableLiveData<Boolean>()
    val oldAndNewPasswordCompare: LiveData<Boolean> = _oldAndNewPasswordCompare

    fun comparePasswords(old: String, newPassword: String) {
        if (old.isNotEmpty() && newPassword.isNotEmpty() && newPassword.length >= 8 && old.length >= 8) {
            _oldAndNewPasswordCompare.value = true
        } else {
            _oldAndNewPasswordCompare.value = false
            context.showToast("Введите корректные данные")
        }
    }

    fun changePassword(request: ChangePasswordRequest) {
        viewModelScope.launch {
            try {
                val response = repository.changePassword(request)
                if (response.body()?.status == "ok") {
                    context.showToast("Пароль успешно изменён")
                } else {
                    context.showToast(response.body()?.message!!)
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }
}
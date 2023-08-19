package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.domain.repository.AuthRepository
import ru.radixit.letsplay.domain.repository.SharedPrefRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val sharedPrefRepository: SharedPrefRepository,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    fun exitFromProfile() {
        viewModelScope.launch {
            try {
                val logout = repository.logout()
             //   if (logout.isSuccessful) {
                    sharedPrefRepository.removeAuthUser()
                //    }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }
}
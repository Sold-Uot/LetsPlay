package ru.radixit.letsplay.presentation.ui.fragments.auth.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.network.request.LoginRequest
import ru.radixit.letsplay.domain.repository.AuthRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import javax.inject.Inject

/**
 * VM для авторизации
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> get() = _success
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error
    private val _progressBarVisibility = MutableLiveData<Boolean>()
    val progressBarVisibility: LiveData<Boolean> get() = _progressBarVisibility

    fun fetchToken(loginRequest: LoginRequest) {
        _error.value = false
        viewModelScope.launch {
            try {
                _progressBarVisibility.value = true
                val response = authRepository.loginCheck(loginRequest)
                if (response.isSuccessful) {
                    _success.value = true
                    sessionManager.saveToken(response.body()?.userId)
                    sessionManager.saveTimeToken(response.body()?.timeToken!!)
                    sessionManager.saveAuthToken(response.body()?.authToken.toString())
                    sessionManager.saveRefreshToken(response.body()?.refreshToken.toString())
                } else {
                    _error.value = true
                    _success.value = false
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                _progressBarVisibility.value = false
            }
        }
    }
}
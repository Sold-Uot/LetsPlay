package ru.radixit.letsplay.presentation.ui.fragments.auth.registration

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.network.request.RegistrationRequest
import ru.radixit.letsplay.data.network.response.RegistrationResponse
import ru.radixit.letsplay.data.network.response.VerificationResponse
import ru.radixit.letsplay.domain.repository.AuthRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

/**
 * VM для регистрации
 */
@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val registrationRepository: AuthRepository,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _checkUsernameVerify = MutableLiveData<Boolean>()
    val checkUsernameVerify: LiveData<Boolean> get() = _checkUsernameVerify

    private val _successVerify = MutableLiveData<Boolean>()
    val successVerify: LiveData<Boolean> get() = _successVerify

    private val _successRegistry = MutableLiveData<Boolean>()
    val successRegistry: LiveData<Boolean> get() = _successRegistry

    private val _progressBarVisibility = MutableLiveData<Boolean>()
    val progressBarVisibility: LiveData<Boolean> get() = _progressBarVisibility

    private val _registrationRequest = MutableLiveData<RegistrationRequest>()
    val registrationRequest: LiveData<RegistrationRequest> get() = _registrationRequest

    fun checkUsername(input: String) {
        viewModelScope.launch {
            try {
                _progressBarVisibility.value = true
                val response = registrationRepository.verify(input, "checkUsername")
                _checkUsernameVerify.value = response.isSuccessful
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    context.showToast(msg)
                }
            } finally {
                _progressBarVisibility.value = false
            }
        }
    }

    fun verify(input: String) {
        viewModelScope.launch {
            try {
                _progressBarVisibility.value = true
                val response = registrationRepository.verify(input.replace("+7", ""), "register")
                if (response.isSuccessful) {
                    sessionManager.saveRegistrationToken(response.body()?.token.toString())
                    _successVerify.value = true
                    context.showToast(response.body()?.debugger?.code.toString())
                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            VerificationResponse::class.java
                        ).status
                    )
                    _successVerify.value = false
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    context.showToast(msg)
                }
            } finally {
                _progressBarVisibility.value = false
            }
        }
    }

    fun register(registrationRequest: RegistrationRequest) {
        viewModelScope.launch {
            try {
                _progressBarVisibility.value = true
                sessionManager.fetchFirebaseToken()?.let {
                    registrationRequest.fcmId = it
                }
                sessionManager.fetchRegistrationToken()?.let {
                    registrationRequest.token = it
                }
                val response = registrationRepository.register(registrationRequest)
                if (response.isSuccessful) {
                    sessionManager.saveToken(response.body()?.userId)
                    sessionManager.saveTimeToken(response.body()?.timeToken!!)
                    sessionManager.saveAuthToken(response.body()?.authToken.toString())
                    sessionManager.saveRefreshToken(response.body()?.refreshToken.toString())
                    _successRegistry.value = true
                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            RegistrationResponse::class.java
                        ).errors!![0].message
                    )
                    _successRegistry.value = false
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    context.showToast(msg)
                }
            } finally {
                _progressBarVisibility.value = false
            }
        }
    }

    fun saveRegisterData(registrationRequest: RegistrationRequest) {
        _registrationRequest.value = registrationRequest
    }

}
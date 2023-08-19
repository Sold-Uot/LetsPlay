package ru.radixit.letsplay.presentation.ui.fragments.auth.forgot

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.network.request.AuthReset
import ru.radixit.letsplay.data.network.request.AuthResetRequest
import ru.radixit.letsplay.data.network.response.ConfirmResponse
import ru.radixit.letsplay.data.network.response.RegistrationResponse
import ru.radixit.letsplay.domain.repository.AuthRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

/**
 * VM для восстановления пароля
 */
@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context,
    private val sessionManager: SessionManager,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    fun verify(input: String) {
        viewModelScope.launch {
            try {
                val response = repository.verify(input, "reset")
                if (response.isSuccessful) {
                    sessionManager.saveForgotEmailOrPhone(input)
                    sessionManager.saveForgotToken(response.body()?.token.toString())
                    _success.value = true
                } else {
                    _success.value = false
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ConfirmResponse::class.java
                        ).message.toString()
                    )
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sessionManagerEmailOrPhone() {
        _data.value = sessionManager.fetchForgotEmailOrPhone()
    }

    fun reset(code: String) {
        viewModelScope.launch {
            val token = sessionManager.fetchForgotToken()
            try {
                val response = repository.reset(
                    AuthReset(
                        token = token.toString(),
                        code = code.toInt()
                    )
                )
                if (response.isSuccessful) {
                    sessionManager.saveToken(response.body()?.userId)
                    sessionManager.saveTimeToken(response.body()?.timeToken!!)
                    sessionManager.saveAuthToken(response.body()?.authToken.toString())
                    sessionManager.saveRefreshToken(response.body()?.refreshToken.toString())
                    sessionManager.removeForgot()
                    _success.value = true
                } else {
                    _success.value = false
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ConfirmResponse::class.java
                        ).message.toString()
                    )
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun password(password: String) {
        viewModelScope.launch {
            _success.value = true
            try {
                val response = repository.password(AuthResetRequest(password = password))
                if (response.isSuccessful) {
                    context.showToast(response.body()?.message.toString())
                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            RegistrationResponse::class.java
                        ).message.toString()
                    )
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
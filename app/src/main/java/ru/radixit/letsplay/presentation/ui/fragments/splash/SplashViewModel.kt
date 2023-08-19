package ru.radixit.letsplay.presentation.ui.fragments.splash

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.domain.global.SingleLiveEvent
import ru.radixit.letsplay.presentation.global.ErrorHandler
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    companion object {
        private const val SPLASH_DELAY_IN_MS = 500L
    }

    private val _splashNavCommand = SingleLiveEvent<SplashNavCommand?>()
    val splashNavCommand: LiveData<SplashNavCommand?> = _splashNavCommand

    init {
        viewModelScope.launch {
            try {
                delay(SPLASH_DELAY_IN_MS)

                val navCommand = if (sessionManager.fetchAuthToken()
                        .isNotEmpty() && sessionManager.fetchAuthToken() != "null"
                ) {
                    SplashNavCommand.NAVIGATE_TO_MAIN
                } else {
                    SplashNavCommand.NAVIGATE_TO_AUTH
                }
                _splashNavCommand.postValue(navCommand)
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
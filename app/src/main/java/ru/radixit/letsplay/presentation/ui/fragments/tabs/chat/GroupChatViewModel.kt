package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

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
import ru.radixit.letsplay.data.model.ChatMessage
import ru.radixit.letsplay.data.model.UserMessage
import ru.radixit.letsplay.data.network.MyWebSocket
import ru.radixit.letsplay.data.network.response.ChatInfoResponse
import ru.radixit.letsplay.domain.repository.ChatRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

/**
 * VM для группового чата
 */
@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val sessionManager: SessionManager,
    private val myWebSocket: MyWebSocket,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _messages = MutableLiveData<List<UserMessage>>()
    val messages: LiveData<List<UserMessage>> = _messages
    private val _chatInfo = MutableLiveData<ChatInfoResponse>()
    val chatInfo: LiveData<ChatInfoResponse> = _chatInfo

    fun fetchChat(receiverId: Int, chatId: Int) {

        viewModelScope.launch {
            try {
                val response = repository.fetchChat(receiverId, chatId)
                if (response.isSuccessful) {
                    _messages.value = response.body()?.list?.reversed()
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendMessage(chatType: Int, receiverId: Int, senderId: Int, message: String) {
        try {
            myWebSocket.send(chatType, receiverId, senderId, message)
        } catch (e: Exception) {
            errorHandler.proceed(e) { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun fetchChatInfo(chatType: Int, receiverId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.fetchChatInfo(chatType, receiverId)
                if (response.isSuccessful) {
                    _chatInfo.value = response.body()
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun fetchMessage(): LiveData<ChatMessage> {
        return myWebSocket.liveData
    }

    fun fetchToken(): Int {
        return sessionManager.fetchToken()
    }

}
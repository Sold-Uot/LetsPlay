package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.domain.repository.ChatRepository
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

/**
 * VM для чата
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private var _refreshState = MutableLiveData<PagingData<Chat>>()

    val refreshState: LiveData<PagingData<Chat>> get() = _refreshState
    fun fetchChats(): Flow<PagingData<Chat>> {
        return repository.fetchChats(ListRequest())
            .map { pagingData -> pagingData.map { it } }
    }







}
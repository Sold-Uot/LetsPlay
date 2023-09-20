package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * VM для чата
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    fun fetchChats(): Flow<PagingData<Chat>> {
        return repository.fetchChats(ListRequest())
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }


}
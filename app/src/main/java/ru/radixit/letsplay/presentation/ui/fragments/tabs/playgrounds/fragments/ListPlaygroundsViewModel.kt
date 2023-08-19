package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.NewNotifCountModel
import ru.radixit.letsplay.data.model.Playground
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.CreatePlaygroundResponse
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject


@HiltViewModel
class ListPlaygroundsViewModel @Inject constructor(
    private val repository: PlaygroundRepository,
) : ViewModel() {

    val playgrounds: Flow<PagingData<Playground>>

    init {
        playgrounds = loadPlaygrounds()
    }

    private val _newNotifCount = MutableLiveData<Int>()
    val newNotifCount: LiveData<Int> = _newNotifCount

    fun searchPlaygrounds(query: String): Flow<PagingData<Playground>> {
        return repository.getListPlaygrounds(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }

    fun newNotifCount() {
        viewModelScope.launch {
            val response = repository.newNotifCount()
            if (response.isSuccessful) {
                response.body()?.let {
                    _newNotifCount.value = it.total
                }
            }
        }
    }


    fun loadPlaygrounds(query: String = ""): Flow<PagingData<Playground>> {
        return repository.getListPlaygrounds(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }

}

sealed class UiModel {
    data class PlaygroundItem(val playground: Playground) : UiModel()
}
package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.PlaygroundForMap
import ru.radixit.letsplay.data.model.PlaygroundInDetail
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val playgroundRepository: PlaygroundRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _maps = MutableLiveData<MapsResponse>()
    val maps: LiveData<MapsResponse> = _maps
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title
    private val _general = MutableLiveData<PlaygroundInDetail.General>()
    val general: LiveData<PlaygroundInDetail.General> = _general
    private val _more = MutableLiveData<PlaygroundInDetail.More>()
    val more: LiveData<PlaygroundInDetail.More> = _more
    private val _photos = MutableLiveData<List<PlaygroundInDetail.Photo>>()
    val photos: LiveData<List<PlaygroundInDetail.Photo>> = _photos
    private val _playgroundForMap = MutableLiveData<PlaygroundForMap>()
    val playgroundForMap: LiveData<PlaygroundForMap> = _playgroundForMap
    private val _newNotifCount = MutableLiveData<Int>()
    val newNotifCount: LiveData<Int> = _newNotifCount

    fun maps() {
        viewModelScope.launch {
            try {
                val response = playgroundRepository.maps()
                if (response.isSuccessful) {
                    _maps.value = response.body()
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun newNotifCount() {
        viewModelScope.launch {
            val response = playgroundRepository.newNotifCount()
            if (response.isSuccessful) {
                response.body()?.let {
                    _newNotifCount.value = it.total
                }
            }
        }
    }
    fun getPlayground(id: String) {
        viewModelScope.launch {
            try {
                val response = playgroundRepository.getPlayground(id)
                if (response.isSuccessful) {
                    _title.value = response.body()?.title
                    _general.value = response.body()?.general
                    _more.value = response.body()?.more
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getPlaygroundForMap(id: Int) {
        viewModelScope.launch {
            try {
                val response = playgroundRepository.getPlaygroundForMap(id)
                if (response.isSuccessful) {
                    _playgroundForMap.value = response.body()
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.network.response.EventForMap
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.domain.repository.EventRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject


@HiltViewModel
class MapListEventsViewModel @Inject constructor(
    private val repository: EventRepository,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _maps = MutableLiveData<List<MapsResponse.Geocodes>>()
    val maps: LiveData<List<MapsResponse.Geocodes>> = _maps
    private val _eventForMap = MutableLiveData<EventForMap>()
    val eventForMap: LiveData<EventForMap> = _eventForMap

    fun maps() {
        viewModelScope.launch {
            try {
                val response = repository.maps()
                if (response.isSuccessful) {
                    for (i in response.body()?.geocodes!!) {
                        _maps.value = listOf(i)
                    }
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getEventForMap(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.getEventForMap(id)
                if (response.isSuccessful) {
                    _eventForMap.value = response.body()
                }
            } catch (e: java.lang.Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }
}

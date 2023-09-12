package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

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
import ru.radixit.letsplay.data.network.request.ReportUserRequest
import ru.radixit.letsplay.data.network.response.CommentResponse
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class PlaygroundInfoViewModel @Inject constructor(
    private val repository: PlaygroundRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _maps = MutableLiveData<MapsResponse>()
    val maps: LiveData<MapsResponse> = _maps
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title
    private val _general = MutableLiveData<PlaygroundInDetail.General>()
    val general: LiveData<PlaygroundInDetail.General> = _general
    private val _playgroundForMap = MutableLiveData<PlaygroundForMap>()
    val playgroundForMap: LiveData<PlaygroundForMap> = _playgroundForMap
    private val _more = MutableLiveData<PlaygroundInDetail.More>()
    val more: LiveData<PlaygroundInDetail.More> = _more
    private val _schedule = MutableLiveData<List<PlaygroundInDetail.Schedule?>>()
    val schedule: LiveData<List<PlaygroundInDetail.Schedule?>> = _schedule
    private val _photos = MutableLiveData<List<PlaygroundInDetail.Photo>>()
    val photos: LiveData<List<PlaygroundInDetail.Photo>> = _photos
    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = _progress
    private val _progressButton = MutableLiveData<Boolean>()
    val progressButton: LiveData<Boolean> = _progressButton
    private val _textMessage = MutableLiveData<String>()
    val textMessage: LiveData<String> = _textMessage
    private val _comments = MutableLiveData<List<CommentResponse.Comment>>()
    val comments: LiveData<List<CommentResponse.Comment>> = _comments
    var playgroundId: String? = null

    fun maps() {
        viewModelScope.launch {
            try {
                val response = repository.maps()
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


    fun getPlayground(id: String) {
        viewModelScope.launch {
            try {
                playgroundId = id
                _progress.value = true
                val response = repository.getPlayground(id)
                if (response.isSuccessful) {
                    _title.value = response.body()?.title
                    _general.value = response.body()?.general
                    _more.value = response.body()?.more
                    _schedule.value = response.body()?.general?.schedule
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                _progress.value = false
            }
        }
    }

    fun getPlaygroundForMap(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPlaygroundForMap(id)
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

    fun fetchPhotos(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.fetchPhotos(id)
                if (response.isSuccessful) {
                    _photos.value = response.body()?.list
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun comment(id: String, cause: Int, text: String? = null) {
        try {
            _progressButton.value = true
            viewModelScope.launch {
                val response = repository.comment(id, ReportUserRequest(cause = cause, text = text))
                if (response.isSuccessful) {
                    _textMessage.value = response.body()?.message
                    context.showToast("Отзыв успешно отправлен")
                }
            }
        } catch (e: Exception) {
            errorHandler.proceed(e) { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        } finally {
            _progressButton.value = false
        }
    }

    fun commentList() {
        viewModelScope.launch {
            try {
                val response = repository.fetchComments(playgroundId!!.toInt())
                if (response.isSuccessful) {
                    _comments.value = response.body()?.list
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

}
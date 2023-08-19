package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.settings.editProfile

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.model.Photo
import ru.radixit.letsplay.data.network.request.EditProfileRedesRequest
import ru.radixit.letsplay.data.network.response.EditProfileResponse
import ru.radixit.letsplay.data.network.response.FetchEditProfileResponse
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.domain.global.ResourceManager
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val resourceManager: ResourceManager,
    private val repository: ProfileRepository,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _sex = MutableLiveData(true)
    private val _position = MutableLiveData(0)
    val position: LiveData<Int> = _position
    private val _textSex = MutableLiveData<String>()
    val textSex: LiveData<String> = _textSex
    private val _textPosition = MutableLiveData<String>()
    val textPosition: LiveData<String> = _textPosition
    private val _uri = MutableLiveData<String>()
    val uri: LiveData<String> = _uri
    private val _uploadingPhoto = MutableLiveData<Boolean>()
    val uploadingPhoto: LiveData<Boolean> = _uploadingPhoto
    private val _profile = MutableLiveData<FetchEditProfileResponse>()
    val profile: LiveData<FetchEditProfileResponse> = _profile
    private val _photo = MutableLiveData<Photo>()
    val photo: LiveData<Photo> = _photo
    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

    fun changeSex() {
        if (_sex.value == true) {
            _textSex.value = resourceManager.getString(R.string.edit_profile_sex_hint_woman)
            _sex.value = false
        } else {
            _textSex.value = resourceManager.getString(R.string.edit_profile_sex_hint)
            _sex.value = true
        }
    }

    fun changePositionOnField() {
        when (_position.value) {
            3 -> {
                _textPosition.value =
                    resourceManager.getString(R.string.edit_profile_position_defender)
                _position.value = 2
            }
            1 -> {
                _textPosition.value =
                    resourceManager.getString(R.string.edit_profile_position_goalkeeper)
                _position.value = 3
            }
            else -> {
                _textPosition.value = resourceManager.getString(R.string.edit_profile_position)
                _position.value = 1
            }
        }
    }

    fun saveUri(uri: String) {
        _uri.value = uri
    }

    fun uploadAvatar(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                _uploadingPhoto.value = true

                val baos = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val imageBytes = baos.toByteArray()

                val base64String: String =
                    Base64.encodeToString(imageBytes, Base64.DEFAULT)
                val response =
                    repository.uploadAvatar(base64File = "data:image/png;base64,${base64String}")
                if (response.isSuccessful) {
                    context.showToast(response.body()?.message.toString())
                } else {
                    context.showToast("Неверный формат изображения")
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                _uploadingPhoto.value = false
            }
        }
    }

    fun edit(request: EditProfileRedesRequest,result:(Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.editProfile(request)
                if (response.isSuccessful) {
                    context.showToast("Изменения успешно сохранены")
                    result(true)
                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            EditProfileResponse::class.java
                        ).errors[0].message
                    )
                    result(false)
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { context.showToast(it) }
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            try {
                val response = repository.fetchProfile()
                if (response.isSuccessful) {
                    _profile.value = response.body()
                    _photo.value = response.body()?.data?.photo!!
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun savePosition(position: Int) {
        _position.value = position
    }

}
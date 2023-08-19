package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.tabs.teams.createTeam

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.CreateTeamRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.request.UploadPhotoChatRequest
import ru.radixit.letsplay.data.network.response.CreateTeamResponse
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.getFileName
import ru.radixit.letsplay.utils.showToast
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateTeamViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedUsers = MutableLiveData<ArrayList<User>>()
    val selectedUsers: LiveData<ArrayList<User>> = _selectedUsers
    private val listUsers = arrayListOf<User>()
    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success
    private val _edit = MutableLiveData<String>()
    val edit: LiveData<String> = _edit
    private val _uri = MutableLiveData<MutableList<String>>()
    private val _uriList = MutableLiveData<List<Uri>>()
    val uriList: LiveData<List<Uri>> = _uriList
    private val _uploadingPhoto = MutableLiveData<Boolean>()
    val uploadingPhoto: LiveData<Boolean> = _uploadingPhoto
    val list = mutableListOf<String>()

    fun createTeam(title: String, nickname: String) {
        viewModelScope.launch {
            try {
                val list = mutableListOf<Member>()
                if (_selectedUsers.value?.isNotEmpty() == true) {
                    for (i in _selectedUsers.value!!) {
                        list.add(Member(userId = i.id))
                    }
                }
                val response = repository.createTeam(CreateTeamRequest(title, nickname, list))
                if (response.isSuccessful) {
                    _success.value = true

                    if (_uri.value!!.isNotEmpty()) {
                        for (base64Url in _uri.value!!) {
                            repository.uploadPhoto(
                                response.body()?.id!!,
                                UploadPhotoChatRequest(
                                    "data:image/png;base64,$base64Url"
                                )
                            )
                        }
                    }
                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            CreateTeamResponse::class.java
                        ).errors[0].message
                    )
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun saveUri(uri: List<Uri>) {
        _uriList.value = uri
        if (Build.VERSION.SDK_INT < 28) {
            for (i in uri) {
                val file = File(
                    context.cacheDir,
                    context.contentResolver.getFileName(i)
                )
                val bitmap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    Uri.fromFile(file)
                )
                uploadAvatar(bitmap)
            }
        } else {
            for (i in uri) {
                val source = ImageDecoder.createSource(context.contentResolver, i)
                val bitmap = ImageDecoder.decodeBitmap(source)
                uploadAvatar(bitmap)
            }
        }
    }

    private fun uploadAvatar(bitmap: Bitmap) {
        try {
            _uploadingPhoto.value = true
            val baos = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            val imageBytes = baos.toByteArray()

            val base64String: String =
                Base64.encodeToString(imageBytes, Base64.DEFAULT)
            list.add(base64String)
            _uploadingPhoto.value = false
            _uri.value = list
        } catch (e: Exception) {
            errorHandler.proceed(e) { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun searchFriends(query: String): Flow<PagingData<User>> {
        return repository.friends(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
    }

    fun searchUsers(query: String = ""): Flow<PagingData<User>> {
        return repository.findFriend(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
            .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(), replay = 10)
    }

    fun remove(user: User) {
        listUsers.remove(user)
        _selectedUsers.value = listUsers
    }

    fun add(user: User) {
        listUsers.add(user)
        _selectedUsers.value = listUsers
    }
}
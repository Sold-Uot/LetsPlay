package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.Calendar
import ru.radixit.letsplay.data.model.FriendEntity
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.request.CreateEventRequest
import ru.radixit.letsplay.data.network.request.ListRequest
import ru.radixit.letsplay.data.network.response.CreateEventResponse
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.data.network.response.Team
import ru.radixit.letsplay.data.network.response.UserForTeamPlayers
import ru.radixit.letsplay.domain.global.ResourceManager
import ru.radixit.letsplay.domain.repository.EventRepository
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import ru.radixit.letsplay.domain.repository.ProfileRepository
import ru.radixit.letsplay.domain.repository.TeamRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.FriendMapper
import ru.radixit.letsplay.utils.Status
import ru.radixit.letsplay.utils.getFileName
import ru.radixit.letsplay.utils.showToast
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val playgroundRepository: PlaygroundRepository,
    private val teamRepository: TeamRepository,
    private val errorHandler: ErrorHandler,
    private val sessionManager: SessionManager,
    private val eventRepository: EventRepository,
    @ApplicationContext private val context: Context,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private val _countFriendAddInInviteList = MutableLiveData<Int>()
    val countFriendAddInInviteList: LiveData<Int> = _countFriendAddInInviteList

    private val _listSelectFriends = MutableLiveData<List<FriendEntity>>()
     val listSelectFriends : LiveData<List<FriendEntity>> get() = _listSelectFriends

    private val _listTeamPlayers = MutableLiveData<List<UserForTeamPlayers>>()
    val listTeamPlayers : LiveData<List<UserForTeamPlayers>> get() =  _listTeamPlayers

    private val _playgId = MutableLiveData<Int>()
    val playgId: LiveData<Int> = _playgId
    private val _maps = MutableLiveData<MapsResponse>()
    val maps: LiveData<MapsResponse> = _maps
    private val _selectedUsers = MutableLiveData<ArrayList<User>>()
    val selectedUsers: LiveData<ArrayList<User>> = _selectedUsers
    private val list = arrayListOf<User>()
    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams
    private val _disabledDays = MutableLiveData<List<String>>()
    val disabledDays: MutableLiveData<List<String>> = _disabledDays
    private val _freeHours = MutableLiveData<String>()
    val freeHours: LiveData<String> = _freeHours
    private val _calendar = MutableLiveData<List<Calendar>>()
    val calendar: LiveData<List<Calendar>> = _calendar
    private val _startAndEnd = MutableLiveData<Pair<Calendar, Calendar?>>()
    val startAndEnd: LiveData<Pair<Calendar, Calendar?>> = _startAndEnd
    private val _start = MutableLiveData<Int>()
    val start: LiveData<Int> = _start
    private val _end = MutableLiveData<Int>()
    val end: LiveData<Int> = _end
    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date
    private val _startString = MutableLiveData<String>()
    val startString: LiveData<String> = _startString
    private val _endString = MutableLiveData<String>()
    val endString: LiveData<String> = _endString

    private val _gameLevel = MutableLiveData(0)
    val gameLevel: LiveData<Int> = _gameLevel

    private val _titleAddress = MutableLiveData<String>()
    val titleAddress: LiveData<String> = _titleAddress

    private val _addressLatLng = MutableLiveData<LatLng>()
    val addressLatLng: LiveData<LatLng> = _addressLatLng

    private val _textGameLevel = MutableLiveData<String>()
    val textGameLevel: LiveData<String> = _textGameLevel

    private val _gameStatus = MutableLiveData(0)
    val gameStatus: LiveData<Int> = _gameStatus

    private val _textGameStatus = MutableLiveData<String>()
    val textGameStatus: LiveData<String> = _textGameStatus

    private val _uri = MutableLiveData<MutableList<String>>()
    private val _uriList = MutableLiveData<List<Uri>>()
    val uriList: LiveData<List<Uri>> = _uriList

    private val _uploadingPhoto = MutableLiveData<Boolean>()
    val uploadingPhoto: LiveData<Boolean> = _uploadingPhoto
    val lists = mutableListOf<String>()

    private val _eventSuccessCreated = MutableLiveData<Boolean>()
    val eventSuccessCreated: LiveData<Boolean> = _eventSuccessCreated



    fun int(value: Int) {
        changeGameLevelRedes(value)
        changeGameStatusRedes(value)
    }

    fun setLatLng(value: LatLng) {
        _addressLatLng.value = value
    }

    fun setTitle(value: String) {
        _titleAddress.value = value
    }

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

    fun changeGameLevel() {
        when (_gameLevel.value) {
            2 -> {
                _textGameLevel.value =
                    resourceManager.getString(R.string.level_game)
                _gameLevel.value = 0
            }

            1 -> {
                _textGameLevel.value =
                    resourceManager.getString(R.string.level_game_1)
                _gameLevel.value = 2
            }

            0 -> {
                _textGameLevel.value = resourceManager.getString(R.string.level_game_2)
                _gameLevel.value = 1
            }
        }
    }

    fun changeGameLevelRedes(value: Int) {
        when (value) {
            2 -> {
                _textGameLevel.value =
                    resourceManager.getString(R.string.level_game)
                _gameLevel.value = 0
            }

            1 -> {
                _textGameLevel.value =
                    resourceManager.getString(R.string.level_game_1)
                _gameLevel.value = 2
            }

            0 -> {
                _textGameLevel.value = resourceManager.getString(R.string.level_game_2)
                _gameLevel.value = 1
            }
        }
    }

    fun changeGameStatus() {
        when (_gameStatus.value) {
            1 -> {
                _textGameStatus.value =
                    resourceManager.getString(R.string.game_status_private)
                _gameStatus.value = 0
            }

            0 -> {
                _textGameStatus.value = resourceManager.getString(R.string.game_status)
                _gameStatus.value = 1
            }
        }
    }

    fun changeGameStatusRedes(value: Int) {
        when (value) {
            1 -> {
                _textGameStatus.value =
                    resourceManager.getString(R.string.game_status_private)
                _gameStatus.value = 0
            }

            0 -> {
                _textGameStatus.value = resourceManager.getString(R.string.game_status)
                _gameStatus.value = 1
            }
        }
    }

    fun searchFriends(query: String = ""): Flow<PagingData<User>> {
        return repository.friends(
            ListRequest(
                search = query,
                userId = sessionManager.fetchToken().toString()
            )
        )
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
            .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(), replay = 10)
    }

    fun searchUsers(query: String = ""): Flow<PagingData<User>> {
        return repository.findFriend(ListRequest(search = query))
            .map { pagingData -> pagingData.map { it } }.cachedIn(viewModelScope)
            .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed(), replay = 10)
    }

    fun remove(user: User) {
        list.remove(user)
        _selectedUsers.value = list
    }

    fun add(user: User) {
        list.add(user)
        _selectedUsers.value = list
    }

    fun createEvent(request: CreateEventRequest, res: (Boolean, Int?) -> Unit) {
        viewModelScope.launch {

            val response = eventRepository.creatEvent(request)
            if (response.isSuccessful) {
                res.invoke(true, response.body()?.eventId)
                _eventSuccessCreated.value = response.isSuccessful
                eventRepository.upload(
                    response.body()?.eventId.toString(),
                    "data:image/png;base64,${_uri.value}"
                )
                /*if (_uri.value != null) {
                    if (_uri.value!!.size <= 10) {
                        for (base64Url in _uri.value!!) {

                            Log.e("image_base64" , base64Url)
                            eventRepository.upload(
                                response.body()?.eventId.toString(),
                                "data:image/png;base64,$base64Url"
                            )
                        }
                    } else {
                        for (base64Url in _uri.value!!.indices) {
                            if (base64Url > 10) {
                                break
                            }
                            Log.e("image_base64" , base64Url.toString())

                            eventRepository.upload(
                                response.body()?.eventId.toString(),
                                "data:image/png;base64,${_uri.value!![base64Url]}"
                            )
                        }
                    }





            }*/
            } else {
                res.invoke(false, null)
                context.showToast(
                    Gson().fromJson(
                        response.errorBody()?.charStream(),
                        CreateEventResponse::class.java
                    ).errors[0].message
                )
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
            lists.add(base64String)
            _uploadingPhoto.value = false
            _uri.value = lists
        } catch (e: Exception) {
            errorHandler.proceed(e) { msg ->
                context.showToast(msg)
            }
        }
    }

    fun listTeams() {
        viewModelScope.launch {
            Log.e("tokenId", sessionManager.fetchAuthToken())
            try {
                val response = repository.listTeams(sessionManager.fetchToken())
                if (response.isSuccessful) {
                    _teams.value = response.body()?.list
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun fetchHoursFree(id: Int, day: String) {
        viewModelScope.launch {
            try {
                val response = playgroundRepository.calendarHours(id, day)
                if (response.isSuccessful) {
                    _freeHours.value = response.body()?.freeHours
                    _calendar.value = response.body()?.calendar?.filterNot { it.disabled }
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun fetchDisabledDays(id: Int, month: String, year: String) {
        viewModelScope.launch {
            try {
                val response = playgroundRepository.fetchDisabledDays(id, month, year)
                if (response.isSuccessful) {
                    _disabledDays.value = response.body()?.disabledDays
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            }
        }
    }

    fun addFriendToInviteList(user: User) {
        viewModelScope.launch {
            runCatching {
                val response = eventRepository.addFriendToInviteList(FriendMapper.mapInFriendEntity(user))
                response.collect{
                    when (it.status){
                        Status.SUCCESS -> {context.showToast("Пользователь добавлен")}
                        Status.ERROR -> {context.showToast("Неудалось добавить пользователя ")}
                        Status.LOADING -> {}
                    }
                }
            }
        }
    }

    fun removeFriendToInviteList(user: User) {
        viewModelScope.launch {
            runCatching {
                val response = eventRepository.removeFriendToInviteList(FriendMapper.mapInFriendEntity(user))
                response.collect{
                    when (it.status){
                        Status.SUCCESS -> {
                            context.showToast("Пользователь удалён")
                        }
                        Status.ERROR -> {context.showToast("Неудалось добавить пользователя ")}
                        Status.LOADING -> {}
                    }
                }
            }
        }

    }

    fun getCountFriendAddInInviteList(){
        viewModelScope.launch {
            try {
                val res  = eventRepository.getCountFriendInDB()
                res.collect{
                    Log.e("323" , it.toString())
                    _countFriendAddInInviteList.value = it
                }

            }
            catch (ex:Exception){

            }
        }
    }

    fun fetchTeamPlayers(id : Int){
        viewModelScope.launch {
           runCatching {
               val response = teamRepository.fetchListTeamPlayers(id)
               response.collect{
                   if(it.isSuccessful){
                       _listTeamPlayers.value = it.body()?.list ?: emptyList()
                   }
               }
           }
        }
    }

    fun fetchFriendList() {

        viewModelScope.launch {
            runCatching {
                val response = eventRepository.fetchFriendToInviteList()
                response.collect{
                    when (it.status){
                        Status.SUCCESS -> {

                                _listSelectFriends.value = it.data!!


                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
        }

    }

    fun savePlaygId(playgId: Int) {
        _playgId.value = playgId
    }

    fun saveStart(start: Int) {
        _start.value = start
    }

    fun saveStartAndEnd(start: Pair<Calendar, Calendar?>) {
        _startAndEnd.value = start
    }

    fun saveEnd(end: Int) {
        _end.value = end
    }

    fun saveBeginTime(time: String) {
        _startString.value = time
    }

    fun saveEndTime(time: String) {
        _endString.value = time
    }

    fun saveDate(date: String) {
        _date.value = date
    }
}
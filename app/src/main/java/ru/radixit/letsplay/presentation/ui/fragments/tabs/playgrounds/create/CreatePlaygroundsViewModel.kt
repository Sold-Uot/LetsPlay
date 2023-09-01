package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.create

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.global.SessionManager
import ru.radixit.letsplay.data.model.FieldSize
import ru.radixit.letsplay.data.model.Geocode
import ru.radixit.letsplay.data.model.Schedule
import ru.radixit.letsplay.data.network.request.CreatePlaygroundRequest
import ru.radixit.letsplay.data.network.request.DaDataRequest
import ru.radixit.letsplay.data.network.response.CreatePlaygroundResponse
import ru.radixit.letsplay.data.network.response.DaDataResponse
import ru.radixit.letsplay.domain.repository.PlaygroundRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.getFileName
import ru.radixit.letsplay.utils.showToast
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePlaygroundsViewModel @Inject constructor(
    private val repository: PlaygroundRepository,
    private val sessionManager: SessionManager,
    private val errorHandler: ErrorHandler,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _availability = MutableLiveData<String>()
    val availability: LiveData<String> = _availability
    private val _availabilityBalls = MutableLiveData<String>()
    private val _availabilityChanging = MutableLiveData<String>()
    private val _availabilityShirts = MutableLiveData<String>()
    private val _availabilityLights = MutableLiveData<String>()
    private val _availabilityShower = MutableLiveData<String>()
    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address
    private val _latLng = MutableLiveData<String>()
    val latLng: LiveData<String> = _latLng
    private val _lonLng = MutableLiveData<String>()
    val lonLng: LiveData<String> = _lonLng
    private val _latLngObj = MutableLiveData<LatLng>()
    val latLngObj: LiveData<LatLng> = _latLngObj
    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber
    private val _coverage = MutableLiveData<Int>()
    val coverage: LiveData<Int> = _coverage
    private val _shoes = MutableLiveData<Boolean>(false)
    val shoes: LiveData<Boolean> = _shoes
    private val _payment = MutableLiveData<Boolean>()
    val payment: LiveData<Boolean> = _payment
    private val _scheduler = MutableLiveData<List<Schedule?>>()
    val scheduler: LiveData<List<Schedule?>> = _scheduler
    private val _playSuccessCreated = MutableLiveData<Boolean>()
    val playSuccessCreated: LiveData<Boolean> = _playSuccessCreated
    private val _uri = MutableLiveData<MutableList<String>>()
    private val _uriList = MutableLiveData<List<Uri>>()
    val uriList: LiveData<List<Uri>> = _uriList
    private val _uploadingPhoto = MutableLiveData<Boolean>()
    val uploadingPhoto: LiveData<Boolean> = _uploadingPhoto
    val list = mutableListOf<String>()

    fun saveAddress(address: String, latLng: String, lonLng: String) {
        _address.value = address
        _latLngObj.value = LatLng(latLng.toDouble(), lonLng.toDouble())
        _latLng.value = latLng
        _lonLng.value = lonLng
    }

    fun saveShoes(shoes: Boolean) {
        _shoes.value = shoes
    }

    fun savePayment(payment: Boolean) {
        _payment.value = payment
    }

    fun saveCoverage(coverage: Int) {
        _coverage.value = coverage
    }

    fun saveScheduler(scheduler: List<Schedule>) {
        _scheduler.value = scheduler
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
                context.showToast(msg)
            }
        }
    }

    fun createPlayground(title: String, phoneNumber: String, price: String) {
        viewModelScope.launch {

            try {


                val request = CreatePlaygroundRequest(
                    _availabilityBalls.value,
                    _availabilityChanging.value,
                    _coverage.value,
                    FieldSize(100, 50),
                    Geocode(_address.value, _latLng.value, _lonLng.value),
                    _availabilityLights.value,
                    _availabilityShirts.value,
                    _payment.value,
                    phoneNumber,

                    price,
                    _scheduler.value?.filterNot { it?.timeEnd == null || it.timeStart == null },
                    _availabilityShower.value,
                    studdedCleats = _shoes.value,
                    title = title
                )
                val response = repository.addPlayground(request)
                if (response.isSuccessful) {
                    context.showToast("Площадка отправлена на модерацию")
                    _playSuccessCreated.value = response.isSuccessful
                    if (_uri.value != null) {
                        if (_uri.value!!.size <= 10) {
                            for (base64Url in _uri.value!!) {
                                repository.upload(
                                    response.body()?.id.toString(),
                                    "data:image/png;base64,$base64Url"
                                )
                            }
                        } else {
                            for (base64Url in _uri.value!!.indices) {
                                if (base64Url > 10) {
                                    break
                                }
                                repository.upload(
                                    response.body()?.id.toString(),
                                    "data:image/png;base64,${_uri.value!![base64Url]}"
                                )
                            }
                        }

                    }

                } else {
                    context.showToast(
                        Gson().fromJson(
                            response.errorBody()?.charStream(),
                            CreatePlaygroundResponse::class.java
                        ).error.last().message
                    )
                }
            } catch (ex: Exception) {
                errorHandler.proceed(ex) { context.showToast(it) }
            }


        }
    }


    fun saveMondayStart(graphic: String?) {
        sessionManager.saveMondayStart(graphic)
    }

    fun saveTuesdayStart(graphic: String?) {
        sessionManager.saveTuesdayStart(graphic)
    }

    fun saveWednesdayStart(graphic: String?) {
        sessionManager.saveWednesdayStart(graphic)
    }

    fun saveThursdayStart(graphic: String?) {
        sessionManager.saveThursdayStart(graphic)
    }

    fun saveFridayStart(graphic: String?) {
        sessionManager.saveFridayStart(graphic)
    }

    fun saveSaturdayStart(graphic: String?) {
        sessionManager.saveSaturdayStart(graphic)
    }

    fun saveSundayStart(graphic: String?) {
        sessionManager.saveSundayStart(graphic)
    }


    fun saveMondayEnd(graphic: String?) {
        sessionManager.saveMondayEnd(graphic)
    }

    fun saveTuesdayEnd(graphic: String?) {
        sessionManager.saveTuesdayEnd(graphic)
    }

    fun saveWednesdayEnd(graphic: String?) {
        sessionManager.saveWednesdayEnd(graphic)
    }

    fun saveThursdayEnd(graphic: String?) {
        sessionManager.saveThursdayEnd(graphic)
    }

    fun saveFridayEnd(graphic: String?) {
        sessionManager.saveFridayEnd(graphic)
    }

    fun saveSaturdayEnd(graphic: String?) {
        sessionManager.saveSaturdayEnd(graphic)
    }

    fun saveSundayEnd(graphic: String?) {
        sessionManager.saveSundayEnd(graphic)
    }


    fun getMondayStart(): String? {
        return sessionManager.getMondayStart()
    }

    fun getTuesdayStart(): String? {
        return sessionManager.getTuesdayStart()
    }

    fun getWednesdayStart(): String? {
        return sessionManager.getWednesdayStart()
    }

    fun getThursdayStart(): String? {
        return sessionManager.getThursdayStart()
    }

    fun getFridayStart(): String? {
        return sessionManager.getFridayStart()
    }

    fun getSaturdayStart(): String? {
        return sessionManager.getSaturdayStart()
    }

    fun getSundayStart(): String? {
        return sessionManager.getSundayStart()
    }


    fun getMondayEnd(): String? {
        return sessionManager.getMondayEnd()
    }

    fun getTuesdayEnd(): String? {
        return sessionManager.getTuesdayEnd()
    }

    fun getWednesdayEnd(): String? {
        return sessionManager.getWednesdayEnd()
    }

    fun getThursdayEnd(): String? {
        return sessionManager.getThursdayEnd()
    }

    fun getFridayEnd(): String? {
        return sessionManager.getFridayEnd()
    }

    fun getSaturdayEnd(): String? {
        return sessionManager.getSaturdayEnd()
    }

    fun getSundayEnd(): String? {
        return sessionManager.getSundayEnd()
    }

    fun saveAvailability(availability: String) {
        var end = ""
        val avail = availability.split(" ")
        end = when {
            avail[1] == "0" -> {
                "Нет"
            }

            avail[1] == "1" -> {
                "Бесплатные"
            }

            else -> {
                "Ecть(+${avail[2]} р)"
            }
        }
        _availability.value = avail[0] + ": " + end
        when (avail[0]) {
            "Мячи" -> {
                val balls = when {
                    avail[1] == "0" -> {
                        "no"
                    }

                    avail[1] == "1" -> {
                        "yes"
                    }

                    else -> {
                        avail[2].toInt()
                    }
                }
                _availabilityBalls.value = balls.toString()
            }

            "Раздевалки" -> {
                val changing = when {
                    avail[1] == "0" -> {
                        "no"
                    }

                    avail[1] == "1" -> {
                        "yes"
                    }

                    else -> {
                        avail[2].toInt()
                    }
                }
                _availabilityChanging.value = changing.toString()
            }

            "Освещение" -> {
                val lights = when {
                    avail[1] == "0" -> {
                        "no"
                    }

                    avail[1] == "1" -> {
                        "yes"
                    }

                    else -> {
                        avail[2].toInt()
                    }
                }
                _availabilityLights.value = lights.toString()
            }

            "Манишики" -> {
                val shirts = when {
                    avail[1] == "0" -> {
                        "no"
                    }

                    avail[1] == "1" -> {
                        "yes"
                    }

                    else -> {
                        avail[2].toInt()
                    }
                }
                _availabilityShirts.value = shirts.toString()
            }
        }
    }

    fun setLights(position: Int, price: String?) {
        val lights = when (position) {
            0 -> {
                "no"
            }

            1 -> {
                "yes"
            }

            else -> {
                price ?: "0"
            }
        }
        _availabilityLights.value = lights
    }

    fun setBalls(position: Int, price: String?) {
        val balls = when (position) {
            0 -> {
                "no"
            }

            1 -> {
                "yes"
            }

            else -> {
                price ?: "0"
            }
        }
        _availabilityBalls.value = balls
    }

    fun setShower(pos: Int, price: String?) {
        val balls = when (pos) {
            0 -> {
                "no"
            }

            1 -> {
                "yes"
            }

            else -> {
                price ?: "0"
            }

        }
        _availabilityShower.value = balls
    }

    fun setShirts(position: Int, price: String?) {
        val balls = when (position) {
            0 -> {
                "no"
            }

            1 -> {
                "yes"
            }

            else -> {
                price ?: "0"
            }
        }
        _availabilityShirts.value = balls
    }

    fun setChanging(position: Int, price: String?) {
        val balls: String = when (position) {
            0 -> {
                "no"
            }

            1 -> {
                "yes"
            }

            else -> {
                price ?: "0"
            }
        }
        _availabilityChanging.value = balls
    }

    private val _addresses = MutableLiveData<DaDataResponse>()
    val addresses: LiveData<DaDataResponse> = _addresses

    fun getAddresses(request: DaDataRequest) {
        viewModelScope.launch {
            try {
                val response = repository.addresses(request)
                if (response.isSuccessful) {
                    _addresses.value = response.body()
                } else {
                    response.errorBody()?.charStream()
                }
            } catch (e: Exception) {
                errorHandler.proceed(e) { msg ->
                    context.showToast(msg)
                }
            } finally {

            }
        }
    }

}

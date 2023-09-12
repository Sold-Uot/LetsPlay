package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.model.Photo
import ru.radixit.letsplay.data.network.response.EventMembersResp
import ru.radixit.letsplay.data.network.response.NewEventDescriptionResponse
import ru.radixit.letsplay.domain.repository.EventRepository
import ru.radixit.letsplay.presentation.global.ErrorHandler
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class EventDescriptionViewModel @Inject constructor(
    private val repository: EventRepository,
    @ApplicationContext private val context: Context,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _eventMember = MutableLiveData<EventMembersResp>()
    val eventMember: LiveData<EventMembersResp> = _eventMember

    private val _eventMemberList = MutableLiveData<List<EventMembersResp.Member>>()
    val eventMemberList: LiveData<List<EventMembersResp.Member>> = _eventMemberList

    private val _eventDescription = MutableLiveData<NewEventDescriptionResponse>()
    val eventDescription: LiveData<NewEventDescriptionResponse> = _eventDescription


    private val _successLoading = MutableLiveData<Boolean>()
    val successLoading: LiveData<Boolean> = _successLoading

    fun getEventDesc(id :String){
        viewModelScope.launch {


                _successLoading.value = true
                val response = repository.getEvent(id)
                response.collect{
                    Log.w(this.javaClass.name,it.isSuccessful.toString())

                    if (it.isSuccessful) {
                        Log.w(this.javaClass.name,it.body().toString())
                        _eventDescription.value = it.body()
                    }
                    else {
                        Log.w("NEW_EVENT_CLASSES" , it.body().toString())
                        _successLoading.value = false
                    }
                }


        }
    }
    fun listEventsMembers(id: String) {
        viewModelScope.launch {
            try {
                _successLoading.value = true
                val response = repository.listEventsMembers(id)
                if (response.isSuccessful) {
                    /*_eventMember.value = response.body()
                    _eventMember.value = response.body()*/
                    Log.e( "list_players"  , response.body()!!.list.toString())
/*
                    _eventMemberList.value = listOf(EventMembersResp.Member(1 , "пктрп" , null , "admino" , "admin"))
*/
                    _eventMemberList.value = response!!.body()!!.list

                }
            } catch (e: Exception) {
                errorHandler.proceed(e) {
                    context.showToast(it)
                }
            } finally {
                _successLoading.value = false
            }

        }
    }

}
package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.radixit.letsplay.data.model.Member
import ru.radixit.letsplay.data.network.response.ListTeamPlayersResponse
import ru.radixit.letsplay.domain.repository.TeamRepository
import ru.radixit.letsplay.utils.showToast
import javax.inject.Inject

@HiltViewModel
class ListTeamPlayersViewModel @Inject constructor(private val teamRepository: TeamRepository,@ApplicationContext private val context: Context) :
    ViewModel() {

    private val _playerList = MutableLiveData<ListTeamPlayersResponse>()
    val playerList: LiveData<ListTeamPlayersResponse> = _playerList

    fun fetchPlayersList(id: Int) {
        viewModelScope.launch {
            val result = runCatching {
                teamRepository.fetchListTeamPlayers(id)

            }.onSuccess {
                it.collect{
                    if (it.isSuccessful) {_playerList.value = it.body()
                    Log.e("response" , it.body().toString())
                    }
                }

            }.onFailure {
                context.showToast(it.message.toString())
            }

        }
    }

    fun deletePLayer(id :Int ,list:List<Member>) {
        viewModelScope.launch {

                val response = teamRepository.deletePlayer(id , list)
                response.collect{
                    if(it.isSuccessful){
                        Log.e("wqwe" ,it.body()?.message.toString() )
                    }
                }

        }
    }
}
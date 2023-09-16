package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.radixit.letsplay.data.model.User

class TestViewModel: ViewModel() {
    private val _selectUser = MutableLiveData<ArrayList<User>>()
    val selectUser : LiveData<ArrayList<User> > =_selectUser

    val list  = arrayListOf<User>()

    fun addUser(user: User) {
        list.add(user)
        _selectUser.value = list
    }

    fun removeUser(user : User){
        list.remove(user)
    }

}
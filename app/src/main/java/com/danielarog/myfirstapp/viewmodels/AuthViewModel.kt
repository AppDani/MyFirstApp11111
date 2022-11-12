package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.repositories.UserRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _currentUser: MutableLiveData<AppUser> = MutableLiveData()
    val currentUser: LiveData<AppUser> = _currentUser

    init {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().uid != null) {
                val user = UserRepository.getUser()
                _currentUser.postValue(user)
            }
        }
    }

    suspend fun getUser() : AppUser? {
        return UserRepository.getUser()
    }

    suspend fun saveUser(appUser: AppUser) {
        return UserRepository.saveProfile(appUser)
    }
}
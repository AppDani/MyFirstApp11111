package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.repositories.UserRepository

class HomeViewModel: ViewModel() {

    private val _topSellersLiveData : MutableLiveData<List<AppUser>> = MutableLiveData()
    val topSellersLiveData : LiveData<List<AppUser>> = _topSellersLiveData

    init {
        UserRepository.listenTopSellers(_topSellersLiveData)
    }
}
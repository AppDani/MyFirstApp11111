package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.ProductRepository
import com.danielarog.myfirstapp.repositories.UserRepository
import kotlinx.coroutines.launch

class ProductsByUserViewModel : ViewModel() {


    private val _productsLiveData: MutableLiveData<MutableList<ShoppingItem>> = MutableLiveData(
        mutableListOf()
    )

    val productsLiveData: MutableLiveData<MutableList<ShoppingItem>> = _productsLiveData

    private val _publisherLiveData: MutableLiveData<AppUser> = MutableLiveData()

    val publisherLiveData: MutableLiveData<AppUser> = _publisherLiveData


    fun getProductsByUserID(id: String) {
        viewModelScope.launch {
            ProductRepository.getAllProductsByUserId(id, _productsLiveData)
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch {
            _publisherLiveData.postValue(UserRepository.getUserById(userId))
        }
    }
}
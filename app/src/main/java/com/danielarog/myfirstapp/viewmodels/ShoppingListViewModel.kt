package com.danielarog.myfirstapp.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.database.ShoppingRepository
import com.danielarog.myfirstapp.models.ShoppingItem
import kotlinx.coroutines.launch

class ShoppingListViewModel : ViewModel() {
    private lateinit var shoppingRepository: ShoppingRepository
    lateinit var shoppingItemsLiveData : LiveData<List<ShoppingItem>>

    fun initializeRepo(context:Context) {
        shoppingRepository = ShoppingRepository(context)
        shoppingItemsLiveData = shoppingRepository.shoppingItemsLiveData
    }

    fun insertItem(item:ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.insertItem(item)
        }
    }
    fun updateItem(item:ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.updateItem(item)
        }
    }
    fun deleteItem(item:ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(item)
        }
    }

}
package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.ViewModel
import com.danielarog.myfirstapp.ProductRepository
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.UserRepository

class AddItemViewModel : ViewModel() {

    suspend fun addItem(shoppingItem: ShoppingItem) {
        val user = UserRepository.getUser()
        user?.let {
            shoppingItem.publisherName = user.name
            shoppingItem.location = user.address.split(",")[0]
            ProductRepository.addItem(shoppingItem)
        }
    }

}
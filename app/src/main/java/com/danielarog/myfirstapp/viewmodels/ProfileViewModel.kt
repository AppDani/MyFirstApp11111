package com.danielarog.myfirstapp.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.dialogs.ProductImageSelection
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.ProductRepository
import com.danielarog.myfirstapp.repositories.UserRepository
import com.firebase.ui.auth.data.model.User
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {

    private var _productsListener: ListenerRegistration? = null

    private val _exceptionsLiveData: MutableLiveData<Exception> = MutableLiveData()
    val exceptionsLiveData: LiveData<Exception> get() = _exceptionsLiveData

    private val _userProductsLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    val userProductsLiveData: LiveData<List<ShoppingItem>> get() = _userProductsLiveData


    init {

        _productsListener = UserRepository.listenToUserProducts(
            _userProductsLiveData,
            _exceptionsLiveData
        )
    }

    suspend fun addItem(
        user: AppUser,
        shoppingItem: ShoppingItem,
        imageUri: Uri?,
        imageByteArray: ByteArray?,
        additionalImages: MutableList<ProductImageSelection>
    ) {
        shoppingItem.publisherName = user.name
        shoppingItem.publisherId = user.uid
        shoppingItem.location = user.address_city
        ProductRepository.addItem(
            shoppingItem, imageUri, imageByteArray,
            additionalImages
        )
    }

    suspend fun editItem(
        user: AppUser,
        shoppingItem: ShoppingItem,
        imageUri: Uri?,
        imageByteArray: ByteArray?
    ) {
        shoppingItem.publisherName = user.name
        shoppingItem.publisherId = user.uid
        shoppingItem.location = user.address_city
        ProductRepository.editItem(
            shoppingItem, imageUri, imageByteArray
        )
    }


    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            ProductRepository.deleteItem(item)
        }
    }

    override fun onCleared() {
        super.onCleared()
        _productsListener?.remove()
    }
}


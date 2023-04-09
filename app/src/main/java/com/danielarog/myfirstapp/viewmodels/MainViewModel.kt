package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.FavoriteItem
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.ProductRepository
import com.danielarog.myfirstapp.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration


class MainViewModel : ViewModel() {

    private var _favorites: MutableLiveData<HashMap<String, FavoriteItem>> =
        MutableLiveData(HashMap())
    val favorites: LiveData<HashMap<String, FavoriteItem>> = _favorites
    private val _favoriteProducts: MutableLiveData<List<ShoppingItem>> =
        MutableLiveData(mutableListOf())
    val favoriteProducts: LiveData<List<ShoppingItem>> = _favoriteProducts
    var favoritesListener: ListenerRegistration? = null

    private val _userLive: MutableLiveData<AppUser> = MutableLiveData()
    val userLive: LiveData<AppUser> get() = _userLive
    private var _userListener: ListenerRegistration? = null
    private val _exceptionsLiveData: MutableLiveData<Exception> = MutableLiveData()
    val exceptionsLiveData: LiveData<Exception> get() = _exceptionsLiveData

    init {
        _userListener = UserRepository.listenToUser(
            _userLive,
            _exceptionsLiveData
        )
        favoritesListener = UserRepository.listenFavorites(_favorites)
    }

    fun isFavorite(id: String): Boolean {
        favorites.value?.let {
            val isFavoriteMarked: Boolean = it.contains(id)
            if (isFavoriteMarked && it[id]?.liked == true)
                return true
        }
        return false
    }

    suspend fun getFavoriteProducts() {
        val productFavorites = mutableListOf<ShoppingItem>()
        favorites.value?.let {
            it.entries.forEach { entry ->
                if (entry.value.category.isNotEmpty() && entry.value.liked) {
                    val product = ProductRepository.getSingle(
                        entry.key,
                        ProductCategory.valueOf(entry.value.category.uppercase()),
                        ProductCategory.SubCategory.valueOf(entry.value.subCategory.uppercase())
                    )
                    product?.let { shoppingItem ->
                            productFavorites.add(shoppingItem)
                    }
                }
            }
        }
        _favoriteProducts.postValue(productFavorites)
    }

    fun addProductToFavorites(
        id: String,
        category: String,
        subCategory: String
    ) {
        FirebaseAuth.getInstance().uid?.let { userId ->

            val objectLike = HashMap<String, Any>()
            objectLike["liked"] = true
            objectLike["category"] = category
            objectLike["subCategory"] = subCategory

            UserRepository.usersCollection
                .document(userId)
                .collection("favorites")
                .document(id)
                .set(objectLike)
        }
    }

    fun removeProductToFavorites(
        id: String,
        category: String,
        subCategory: String
    ) {
        FirebaseAuth.getInstance().uid?.let { userId ->
            val objectLike = HashMap<String, Any>()
            objectLike["liked"] = false
            objectLike["category"] = category
            objectLike["subCategory"] = subCategory
            UserRepository.usersCollection
                .document(userId)
                .collection("favorites")
                .document(id)
                .set(objectLike)
        }
    }


    override fun onCleared() {
        super.onCleared()
        favoritesListener?.remove()
        favoritesListener = null
        _userListener?.remove()
        _userListener = null
    }

}
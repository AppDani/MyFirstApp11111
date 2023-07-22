package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.CategoryPair
import com.danielarog.myfirstapp.repositories.ProductRepository
import com.danielarog.myfirstapp.repositories.UserRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ShoppingListViewModel : ViewModel() {

    var fullList: List<ShoppingItem>? = null

    private var _shoppingItemsLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    var shoppingItemsLiveData: LiveData<List<ShoppingItem>> = _shoppingItemsLiveData

    private var _exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    var exceptionLiveData: LiveData<Exception> = _exceptionLiveData

    private val shoppingListItemsCache: HashMap<String, List<ShoppingItem>> = HashMap()


    /*fun filterByColor(color :String) {
        if(fullList == null) {
            fullList = shoppingItemsLiveData.value
        }
        val tempList = ArrayList(shoppingItemsLiveData.value ?: listOf())

        tempList.filter { it -> it. }
    } */
    fun filterBySize(size: String) {
        if (fullList == null) {
            fullList = shoppingItemsLiveData.value
        }

        val tempList =
            ArrayList(shoppingItemsLiveData.value ?: listOf()).filter {
                it.size?.lowercase() == size.lowercase() // Ignore case
            }
        _shoppingItemsLiveData.postValue(tempList)
    }


    fun resetListToFull() {
        if (fullList == null) return
        _shoppingItemsLiveData.postValue(fullList)
        fullList = null
    }


    fun getProductsByUserId(id : String) {

    }

    fun getAllProducts(
        categoryPair: CategoryPair,
        gender: String,
        loading: (() -> Unit)? = null,
        callback: (() -> Unit)? = null
    ) {

        // Items for this subcategory were cached in earlier use
        if (shoppingListItemsCache.containsKey(categoryPair.second.value)) {
            _shoppingItemsLiveData.postValue(
                shoppingListItemsCache[categoryPair.second.value]
            )
            return
        }
        if (categoryPair.second == ProductCategory.SubCategory.ALL) {
            loading?.invoke()
            viewModelScope.launch {
                ProductRepository.getAllCategoryProducts(
                    categoryPair.first,
                    gender,
                    liveData = _shoppingItemsLiveData
                )
                callback?.invoke()
            }
            return
        }

        ProductRepository.listenAllCategoryProductsBySubCategory(
            categoryPair.first, /* category */
            categoryPair.second /* sub category*/
        ) { products, error ->
            // cache the products for later use
            products?.let {
                if (it.isNotEmpty())
                    shoppingListItemsCache[categoryPair.second.value] = it
            }
            if (products != null)
                _shoppingItemsLiveData.postValue(products)
            else if (error != null)
                _exceptionLiveData.postValue(error)
        }
    }



}
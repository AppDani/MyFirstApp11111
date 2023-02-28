package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.CategoryPair
import com.danielarog.myfirstapp.repositories.ProductRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class ShoppingListViewModel : ViewModel() {

    private var _shoppingItemsLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    var shoppingItemsLiveData: LiveData<List<ShoppingItem>> = _shoppingItemsLiveData

    private var _exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    var exceptionLiveData: LiveData<Exception> = _exceptionLiveData


    init {
        viewModelScope.launch {
            ProductRepository.getAllCategoryProductsAll(liveData = _shoppingItemsLiveData)
        }
    }

    fun getAllProducts(
        categoryPair: CategoryPair,
        gender: String,
        loading: (() -> Unit)? = null,
        callback: (() -> Unit)? = null
    ) {
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
            println(products)
            if (products != null)
                _shoppingItemsLiveData.postValue(products)
            else if (error != null)
                _exceptionLiveData.postValue(error)
        }
    }

}
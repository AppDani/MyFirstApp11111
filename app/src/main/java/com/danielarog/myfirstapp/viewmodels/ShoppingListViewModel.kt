package com.danielarog.myfirstapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danielarog.myfirstapp.ProductCategory
import com.danielarog.myfirstapp.ProductRepository
import com.danielarog.myfirstapp.models.ShoppingItem
import java.lang.Exception

class ShoppingListViewModel : ViewModel() {

    private var _shoppingItemsLiveData: MutableLiveData<List<ShoppingItem>> = MutableLiveData()
    var shoppingItemsLiveData: LiveData<List<ShoppingItem>> = _shoppingItemsLiveData

    private var _shoppingItemsSubCategoryLiveData: MutableLiveData<List<Pair<ProductCategory,ProductCategory.SubCategory>>> = MutableLiveData()
    var shoppingItemsSubCategoryLiveData: LiveData<List<Pair<ProductCategory,ProductCategory.SubCategory>>> = _shoppingItemsSubCategoryLiveData


    private var _exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    var exceptionLiveData: LiveData<Exception> = _exceptionLiveData


    init {
        // DEFAULT
        changeCategory(ProductCategory.SHOES)


    }

    fun changeCategory(category: ProductCategory) {
        ProductRepository.listenAllSubCategories(category) { subCategories, error ->
            if (subCategories != null)
                _shoppingItemsSubCategoryLiveData.postValue(subCategories)
            else if (error != null)
                _exceptionLiveData.postValue(error)
        }
    }

    fun changeSubCategory(categoryPair: Pair<ProductCategory,ProductCategory.SubCategory>) {
        ProductRepository.listenAllCategoryProducts(
            categoryPair.first, /* category */
            categoryPair.second /* sub category*/) {
                products, error ->
            if (products != null)
                _shoppingItemsLiveData.postValue(products)
            else if (error != null)
                _exceptionLiveData.postValue(error)
        }
    }

}
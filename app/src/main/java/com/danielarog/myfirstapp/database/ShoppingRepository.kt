package com.danielarog.myfirstapp.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.danielarog.myfirstapp.models.ShoppingItem

class ShoppingRepository(context: Context) {
    private val dao: ItemsDao = ItemsDatabase.getDBInstance(context).dao()
    lateinit var shoppingItemsLiveData : LiveData<List<ShoppingItem>>
    init { getAllItems() }

    suspend fun insertItem(item: ShoppingItem) {
        dao.insertItem(item)
    }

    suspend fun updateItem(item: ShoppingItem) {
        dao.updateItem(item)
    }

    suspend fun deleteItem(item: ShoppingItem) {
        dao.deleteItem(item)
    }

    private fun getAllItems() {
        shoppingItemsLiveData = dao.getAllItems()
    }
}
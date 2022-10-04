package com.danielarog.myfirstapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.danielarog.myfirstapp.models.ShoppingItem


@Dao
interface ItemsDao {

    @Query("SELECT * from shoppingItems")
    fun getAllItems() : LiveData<List<ShoppingItem>>

    @Insert
    suspend fun insertItem(item:ShoppingItem)

    @Delete
    suspend fun deleteItem(item:ShoppingItem)

    @Update
    suspend fun updateItem(item:ShoppingItem)

}
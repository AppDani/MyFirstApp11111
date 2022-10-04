package com.danielarog.myfirstapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "shoppingItems")
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val itemName:String,
    val type:String,
    val date:String,
    val price:String,
    val image:String="",
    val comments:String ="",
)

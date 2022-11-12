package com.danielarog.myfirstapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "shoppingItems")
data class ShoppingItem(
    @PrimaryKey val id: String,
    var itemName: String,
    var gender: String,
    var category: String,
    var subCategory: String,
    var condition: String,
    var location: String,
    var publisherName: String,
    var date: String,
    var price: String,
    var image: String = "",
    var description: String = "",
) {
    constructor() : this("", "","", "", "", "", "", "", "", "", "")
}

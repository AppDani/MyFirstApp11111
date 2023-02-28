package com.danielarog.myfirstapp.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "shoppingItems")
data class ShoppingItem(
    @PrimaryKey val id: String? = "0",
    var itemName: String? = "",
    var gender: String? = "",
    var category: String? = "",
    var subCategory: String? = "",
    var condition: String? = "",
    var size: String? = "",
    var publisherRating: Long = 0,
    var location: String? = "",
    var publisherName: String? = "",
    var publisherId: String? = "",
    var date: String? = "",
    var price: String? = "",
    var image: String? = "",
    var description: String? = "",
    var additionalImages: List<String>? = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    ) {
    }

    constructor() : this("", "", "", "", "", "", "", 0, "", "", "", "", "", "", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(itemName)
        parcel.writeString(gender)
        parcel.writeString(category)
        parcel.writeString(subCategory)
        parcel.writeString(condition)
        parcel.writeString(size)
        parcel.writeLong(publisherRating)
        parcel.writeString(location)
        parcel.writeString(publisherName)
        parcel.writeString(publisherId)
        parcel.writeString(date)
        parcel.writeString(price)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeStringList(additionalImages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingItem> {
        override fun createFromParcel(parcel: Parcel): ShoppingItem {
            return ShoppingItem(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingItem?> {
            return arrayOfNulls(size)
        }
    }
}

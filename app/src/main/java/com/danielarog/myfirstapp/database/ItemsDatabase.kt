package com.danielarog.myfirstapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.danielarog.myfirstapp.models.ShoppingItem

@Database(entities = [ShoppingItem::class], version = 1)
abstract class ItemsDatabase : RoomDatabase() {

    abstract fun dao() : ItemsDao

    companion object {
        private var instance : ItemsDatabase? = null
        fun getDBInstance(context:Context) : ItemsDatabase {
            if(instance == null) {
                instance = Room.databaseBuilder(context,
                     ItemsDatabase::class.java,"itemsDB")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}
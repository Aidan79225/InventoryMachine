package com.aidan.inventoryworkplatform.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aidan.inventoryworkplatform.base.InventoryApplication
import com.aidan.inventoryworkplatform.Entity.WordName

@Database(entities = [WordName::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getWordNameDAO(): WordNameDAO

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(): AppDatabase {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                                InventoryApplication.application,
                                AppDatabase::class.java,
                                AppDatabase::class.java.simpleName
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}
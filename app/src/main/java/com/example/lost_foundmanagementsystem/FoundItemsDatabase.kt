package com.example.lost_foundmanagementsystem

import androidx.room.Database
import androidx.room.RoomDatabase

//database for found items
@Database(
    entities = [FoundItemsEntity::class],
    version = 1
)
abstract class FoundItemsDatabase : RoomDatabase() {
    abstract val dao: FoundItemsDataAccessObject
}
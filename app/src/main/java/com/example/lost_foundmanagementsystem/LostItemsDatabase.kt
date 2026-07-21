package com.example.lost_foundmanagementsystem

import androidx.room.Database
import androidx.room.RoomDatabase

@Database( //identifies this as database for lost_items
    entities = [LostItemsEntity::class],
    version = 1
)
abstract class LostItemsDatabase : RoomDatabase() {

    abstract val dao: LostItemsDataAccessObject;

}
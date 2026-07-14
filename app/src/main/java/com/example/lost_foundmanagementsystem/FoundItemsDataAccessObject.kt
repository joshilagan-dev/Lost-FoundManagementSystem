package com.example.lost_foundmanagementsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

//allows for operations on the found_items database
@Dao
interface FoundItemsDataAccessObject {

    @Upsert //insert and update function
    fun upsertFoundItem(contact: FoundItemsEntity)

    @Delete
    fun deleteFoundItem(contact: FoundItemsEntity)

}
package com.example.lost_foundmanagementsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

//allows for the user to access and perform operations on the lost_items database
@Dao
interface LostItemsDataAccessObject {

    @Upsert //insert and update function
    fun upsertLostItem(contact: LostItemsEntity)

    @Delete
    fun deleteLostItem(contact: LostItemsEntity)
}
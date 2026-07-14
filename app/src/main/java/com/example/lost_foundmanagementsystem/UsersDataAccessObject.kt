package com.example.lost_foundmanagementsystem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert

//this allows the user to access the database and perform operations on it
@Dao
interface UsersDataAccessObject {

    @Upsert //allows for insertion and update within the user database
    fun upsertUser(contact: UsersEntity)

    @Delete
    fun deleteUser(contact: UsersEntity)
}
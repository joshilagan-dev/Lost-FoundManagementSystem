package com.example.lost_foundmanagementsystem

import androidx.room.Database
import androidx.room.RoomDatabase

@Database( //identifies that this is a database for users
    entities = [UsersEntity::class],
    version = 1
)
abstract class UsersDatabase : RoomDatabase() {

    abstract val dao: UsersDataAccessObject

}
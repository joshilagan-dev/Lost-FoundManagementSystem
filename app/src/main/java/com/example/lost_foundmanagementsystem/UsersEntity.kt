package com.example.lost_foundmanagementsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity for database to store the users information
@Entity(tableName = "users")
data class UsersEntity(
    val firstName: String,
    val lastName: String,
    val userPassword: String,
    val userType: String,

    @PrimaryKey(autoGenerate = false)
    val id: Int //unique ID for each user (primary key)
)

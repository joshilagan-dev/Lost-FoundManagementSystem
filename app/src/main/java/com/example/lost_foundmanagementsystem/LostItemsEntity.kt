package com.example.lost_foundmanagementsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lost_items")
data class LostItemsEntity(

    val itemName: String,
    val itemDescription: String,
    val reportedByUser: String,
    val reportedDate: String,

    @PrimaryKey(autoGenerate = false)
    val itemID: Int //unique ID for each item (primary key)

)

package com.example.lost_foundmanagementsystem

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "found_items")
data class FoundItemsEntity(

    val itemName: String,
    val itemDescription: String,
    val foundByUser: String,
    val foundedDate: String,

    @PrimaryKey(autoGenerate = false)
    val itemID: Int //unique ID for each found item (primary key)

)

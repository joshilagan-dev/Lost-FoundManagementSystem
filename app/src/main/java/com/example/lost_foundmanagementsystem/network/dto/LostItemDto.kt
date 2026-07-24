package com.example.lost_foundmanagementsystem.data.network.dto

data class LostItemDto(
    val lost_item_id: Int,
    val item_name: String,
    val item_description: String,
    val lost_location: String,
    val lost_date: String,
    val reported_by: String,
    val reported_date: String,
    val status: String
)
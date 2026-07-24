package com.example.lost_foundmanagementsystem.data.network.dto

data class FoundItemDto(
    val found_item_id: Int,
    val item_name: String,
    val item_description: String,
    val found_location: String,
    val found_date: String,
    val found_by: String,
    val reported_date: String,
    val status: String
)
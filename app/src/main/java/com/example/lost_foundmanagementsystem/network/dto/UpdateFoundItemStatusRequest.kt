package com.example.lost_foundmanagementsystem.data.network.dto

data class UpdateFoundItemStatusRequest(
    val found_item_id: Int,
    val status: String
)

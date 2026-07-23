package com.example.lost_foundmanagementsystem.data.network.dto

data class ClaimItemRequest(
    val found_item_id: Int,
    val claimant_user_id: Int,
    val lost_location_answer: String,
    val lost_date_answer: String
)
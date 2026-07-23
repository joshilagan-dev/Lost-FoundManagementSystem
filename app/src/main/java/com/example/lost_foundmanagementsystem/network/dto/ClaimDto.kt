package com.example.lost_foundmanagementsystem.data.network.dto

data class ClaimDto(
    val claim_id: Int,
    val found_item_id: Int,
    val claimant_user_id: Int,
    val claimant_name: String,
    val item_name: String,
    val lost_location_answer: String,
    val lost_date_answer: String,
    val status: String,
    val submitted_at: String
)
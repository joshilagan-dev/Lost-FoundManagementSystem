package com.example.lost_foundmanagementsystem.data.network.dto

data class UpdateClaimRequest(
    val claim_id: Int,
    val status: String
)
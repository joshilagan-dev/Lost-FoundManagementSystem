package com.example.lost_foundmanagementsystem.data.network.dto

data class ReportLostRequest(
    val user_id: Int,
    val item_name: String,
    val item_description: String,
    val lost_location: String,
    val lost_date: String
)
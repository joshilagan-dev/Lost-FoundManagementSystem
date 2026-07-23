package com.example.lost_foundmanagementsystem.data.network.dto

data class ReportFoundRequest(
    val user_id: Int,
    val item_name: String,
    val item_description: String,
    val found_location: String,
    val found_date: String
)
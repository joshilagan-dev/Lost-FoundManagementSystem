package com.example.lost_foundmanagementsystem.data.network.dto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto?
)

data class UserDto(
    val user_id: Int,
    val stud_number: String,
    val first_name: String,
    val last_name: String,
    val user_type: String,
    val created_at: String
)
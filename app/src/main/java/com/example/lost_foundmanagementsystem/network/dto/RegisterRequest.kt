package com.example.lost_foundmanagementsystem.data.network.dto

data class RegisterRequest(
    val studNumber: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val userType: String
)
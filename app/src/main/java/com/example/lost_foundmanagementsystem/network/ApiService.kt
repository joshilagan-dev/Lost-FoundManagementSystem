package com.example.lost_foundmanagementsystem.network

interface ApiService {
    @POST("login.php")
    suspend fun login()
}
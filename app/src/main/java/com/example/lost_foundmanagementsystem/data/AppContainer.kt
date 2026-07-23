package com.example.lost_foundmanagementsystem.data

import com.example.lost_foundmanagementsystem.data.repository.LostFoundRepository

object AppContainer {

    val repository by lazy {
        LostFoundRepository()
    }
}
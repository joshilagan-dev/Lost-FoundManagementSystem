package com.example.lost_foundmanagementsystem.data

import com.example.lost_foundmanagementsystem.data.fake.FakeLostFoundRepository

object AppContainer {
    val repository: FakeLostFoundRepository by lazy { FakeLostFoundRepository() }
}

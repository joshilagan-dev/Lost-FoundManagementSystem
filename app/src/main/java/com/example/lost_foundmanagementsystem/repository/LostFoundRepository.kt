package com.example.lost_foundmanagementsystem.data.repository

import com.example.lost_foundmanagementsystem.data.network.RetrofitClient
import com.example.lost_foundmanagementsystem.data.network.dto.LoginRequest
import com.example.lost_foundmanagementsystem.data.network.dto.RegisterRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ReportLostRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ReportFoundRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ClaimItemRequest
import com.example.lost_foundmanagementsystem.data.network.dto.UpdateClaimRequest
class LostFoundRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(
        studNumber: String,
        password: String
    ) = api.login(
        LoginRequest(
            studNumber = studNumber,
            password = password
        )
    )

    suspend fun register(
        studNumber: String,
        firstName: String,
        lastName: String,
        password: String,
        userType: String
    ) = api.register(
        RegisterRequest(
            studNumber = studNumber,
            firstName = firstName,
            lastName = lastName,
            password = password,
            userType = userType
        )
    )

    suspend fun reportLost(
        request: ReportLostRequest
    ) = api.reportLost(request)

    suspend fun reportFound(
        request: ReportFoundRequest
    ) = api.reportFound(request)

    suspend fun getLostItems() =
        api.getLostItems()

    suspend fun getFoundItems() =
        api.getFoundItems()

    suspend fun claimItem(
        request: ClaimItemRequest
    ) = api.claimItem(request)

    suspend fun getClaims() =
        api.getClaims()

    suspend fun updateClaim(
        request: UpdateClaimRequest
    ) = api.updateClaim(request)

}
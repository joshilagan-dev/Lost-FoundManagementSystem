package com.example.lost_foundmanagementsystem.data.network

import com.example.lost_foundmanagementsystem.data.network.dto.LoginRequest
import com.example.lost_foundmanagementsystem.data.network.dto.LoginResponse
import com.example.lost_foundmanagementsystem.data.network.dto.RegisterRequest
import com.example.lost_foundmanagementsystem.data.network.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

import com.example.lost_foundmanagementsystem.data.network.dto.BasicResponse
import com.example.lost_foundmanagementsystem.data.network.dto.FoundItemDto
import com.example.lost_foundmanagementsystem.data.network.dto.LostItemDto
import com.example.lost_foundmanagementsystem.data.network.dto.ReportFoundRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ReportLostRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ClaimItemRequest
import retrofit2.http.GET
import com.example.lost_foundmanagementsystem.data.network.dto.ClaimDto
import com.example.lost_foundmanagementsystem.data.network.dto.UpdateClaimRequest
interface ApiService {

    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("reportLost.php")
    suspend fun reportLost(
        @Body request: ReportLostRequest
    ): Response<BasicResponse>

    @POST("reportFound.php")
    suspend fun reportFound(
        @Body request: ReportFoundRequest
    ): Response<BasicResponse>

    @GET("getLostItems.php")
    suspend fun getLostItems(): Response<List<LostItemDto>>

    @GET("getFoundItems.php")
    suspend fun getFoundItems(): Response<List<FoundItemDto>>

    @POST("claimItem.php")
    suspend fun claimItem(
        @Body request: ClaimItemRequest
    ): Response<BasicResponse>

    @GET("getClaims.php")
    suspend fun getClaims(): Response<List<ClaimDto>>

    @POST("updateItem.php")
    suspend fun updateClaim(
        @Body request: UpdateClaimRequest
    ): Response<BasicResponse>

}
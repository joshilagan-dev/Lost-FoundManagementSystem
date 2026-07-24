package com.example.lost_foundmanagementsystem.data.models

enum class UserType {
    Student,
    Staff
}

data class User(
    val id: Int,
    val studNumber: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val userType: UserType,
    val createdAt: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
}

enum class ItemStatus {
    Open,
    Available,
    ClaimPending,
    Claimed,
    Matched,
    Closed
}

data class LostItem(
    val itemId: Int,
    val itemName: String,
    val itemDescription: String,
    val lostLocation: String,
    val lostDate: String,
    val reportedByUser: String,
    val reportedDate: String,
    val status: ItemStatus = ItemStatus.Open
)

data class FoundItem(
    val itemId: Int,
    val itemName: String,
    val itemDescription: String,
    val foundLocation: String,
    val foundDate: String,
    val foundByUser: String,
    val reportedDate: String,
    val status: ItemStatus = ItemStatus.Available
)

enum class ClaimStatus {
    Pending,
    Approved,
    Rejected,
    Claimed
}

data class Claim(
    val claimId: Int,
    val foundItemId: Int,
    val claimantUserId: Int,
    val claimantName: String,
    val lostLocationAnswer: String,
    val lostDateAnswer: String,
    val status: ClaimStatus = ClaimStatus.Pending,
    val submittedAt: String
)

sealed class ItemKind {
    data class Lost(val item: LostItem) : ItemKind()
    data class Found(val item: FoundItem) : ItemKind()
}

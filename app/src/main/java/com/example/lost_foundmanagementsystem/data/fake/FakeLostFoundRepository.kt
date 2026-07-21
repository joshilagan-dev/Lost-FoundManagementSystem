package com.example.lost_foundmanagementsystem.data.fake

import com.example.lost_foundmanagementsystem.data.models.Claim
import com.example.lost_foundmanagementsystem.data.models.ClaimStatus
import com.example.lost_foundmanagementsystem.data.models.FoundItem
import com.example.lost_foundmanagementsystem.data.models.ItemStatus
import com.example.lost_foundmanagementsystem.data.models.LostItem
import com.example.lost_foundmanagementsystem.data.models.User
import com.example.lost_foundmanagementsystem.data.models.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

/**
 * In-memory store so the frontend can be demoed without a backend.
 * Swap this out later for Room or a REST repository.
 */
class FakeLostFoundRepository {

    private val nextUserId = AtomicInteger(3)
    private val nextLostId = AtomicInteger(4)
    private val nextFoundId = AtomicInteger(4)
    private val nextClaimId = AtomicInteger(2)

    private val _users = MutableStateFlow(
        listOf(
            User(1, "Alex", "Student", "student123", UserType.Student),
            User(2, "Jordan", "Staff", "staff123", UserType.Staff)
        )
    )
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _lostItems = MutableStateFlow(
        listOf(
            LostItem(
                itemId = 1,
                itemName = "Blue Umbrella",
                itemDescription = "Compact foldable umbrella with a wooden handle",
                lostLocation = "Library, 2nd floor study area",
                lostDate = "2026-07-10",
                reportedByUser = "Alex Student",
                reportedDate = "2026-07-11",
                status = ItemStatus.Open
            ),
            LostItem(
                itemId = 2,
                itemName = "Student ID Card",
                itemDescription = "MMCL ID with blue lanyard",
                lostLocation = "Cafeteria near the payment counter",
                lostDate = "2026-07-14",
                reportedByUser = "Alex Student",
                reportedDate = "2026-07-14",
                status = ItemStatus.Open
            ),
            LostItem(
                itemId = 3,
                itemName = "Black Power Bank",
                itemDescription = "10,000mAh Anker power bank",
                lostLocation = "Room B204",
                lostDate = "2026-07-15",
                reportedByUser = "Alex Student",
                reportedDate = "2026-07-16",
                status = ItemStatus.Open
            )
        )
    )
    val lostItems: StateFlow<List<LostItem>> = _lostItems.asStateFlow()

    private val _foundItems = MutableStateFlow(
        listOf(
            FoundItem(
                itemId = 1,
                itemName = "Silver Wristwatch",
                itemDescription = "Analog watch with a metal band",
                foundLocation = "Gym locker hallway",
                foundDate = "2026-07-12",
                foundByUser = "Jordan Staff",
                reportedDate = "2026-07-12",
                status = ItemStatus.Available
            ),
            FoundItem(
                itemId = 2,
                itemName = "Red Tumbler",
                itemDescription = "Insulated tumbler with MMCL sticker",
                foundLocation = "Building A, Room 101",
                foundDate = "2026-07-13",
                foundByUser = "Jordan Staff",
                reportedDate = "2026-07-13",
                status = ItemStatus.Available
            ),
            FoundItem(
                itemId = 3,
                itemName = "Wireless Earbuds",
                itemDescription = "White earbuds case, slightly scratched",
                foundLocation = "Parking lot near gate 2",
                foundDate = "2026-07-17",
                foundByUser = "Alex Student",
                reportedDate = "2026-07-17",
                status = ItemStatus.Available
            )
        )
    )
    val foundItems: StateFlow<List<FoundItem>> = _foundItems.asStateFlow()

    private val _claims = MutableStateFlow(
        listOf(
            Claim(
                claimId = 1,
                foundItemId = 1,
                claimantUserId = 1,
                claimantName = "Alex Student",
                lostLocationAnswer = "Gym locker hallway",
                lostDateAnswer = "2026-07-11",
                status = ClaimStatus.Pending,
                submittedAt = "2026-07-18"
            )
        )
    )
    val claims: StateFlow<List<Claim>> = _claims.asStateFlow()

    fun login(firstName: String, password: String): User? {
        return _users.value.firstOrNull {
            it.firstName.equals(firstName.trim(), ignoreCase = true) &&
                it.password == password
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        password: String,
        userType: UserType
    ): Result<User> {
        val exists = _users.value.any {
            it.firstName.equals(firstName.trim(), ignoreCase = true) &&
                it.lastName.equals(lastName.trim(), ignoreCase = true)
        }
        if (exists) {
            return Result.failure(IllegalStateException("An account with that name already exists."))
        }
        val user = User(
            id = nextUserId.getAndIncrement(),
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            password = password,
            userType = userType
        )
        _users.update { it + user }
        return Result.success(user)
    }

    fun addLostItem(
        itemName: String,
        itemDescription: String,
        lostLocation: String,
        lostDate: String,
        reportedByUser: String
    ): LostItem {
        val item = LostItem(
            itemId = nextLostId.getAndIncrement(),
            itemName = itemName.trim(),
            itemDescription = itemDescription.trim(),
            lostLocation = lostLocation.trim(),
            lostDate = lostDate.trim(),
            reportedByUser = reportedByUser,
            reportedDate = today(),
            status = ItemStatus.Open
        )
        _lostItems.update { listOf(item) + it }
        return item
    }

    fun addFoundItem(
        itemName: String,
        itemDescription: String,
        foundLocation: String,
        foundDate: String,
        foundByUser: String
    ): FoundItem {
        val item = FoundItem(
            itemId = nextFoundId.getAndIncrement(),
            itemName = itemName.trim(),
            itemDescription = itemDescription.trim(),
            foundLocation = foundLocation.trim(),
            foundDate = foundDate.trim(),
            foundByUser = foundByUser,
            reportedDate = today(),
            status = ItemStatus.Available
        )
        _foundItems.update { listOf(item) + it }
        return item
    }

    fun submitClaim(
        foundItemId: Int,
        claimant: User,
        lostLocationAnswer: String,
        lostDateAnswer: String
    ): Result<Claim> {
        val found = _foundItems.value.firstOrNull { it.itemId == foundItemId }
            ?: return Result.failure(IllegalArgumentException("Found item not found."))
        if (found.status != ItemStatus.Available) {
            return Result.failure(IllegalStateException("This item is not available to claim."))
        }
        val claim = Claim(
            claimId = nextClaimId.getAndIncrement(),
            foundItemId = foundItemId,
            claimantUserId = claimant.id,
            claimantName = claimant.fullName,
            lostLocationAnswer = lostLocationAnswer.trim(),
            lostDateAnswer = lostDateAnswer.trim(),
            status = ClaimStatus.Pending,
            submittedAt = today()
        )
        _claims.update { listOf(claim) + it }
        _foundItems.update { items ->
            items.map {
                if (it.itemId == foundItemId) it.copy(status = ItemStatus.ClaimPending) else it
            }
        }
        return Result.success(claim)
    }

    fun resolveClaim(claimId: Int, approve: Boolean): Result<Unit> {
        val claim = _claims.value.firstOrNull { it.claimId == claimId }
            ?: return Result.failure(IllegalArgumentException("Claim not found."))
        if (claim.status != ClaimStatus.Pending) {
            return Result.failure(IllegalStateException("Claim was already resolved."))
        }
        val newClaimStatus = if (approve) ClaimStatus.Approved else ClaimStatus.Rejected
        val newItemStatus = if (approve) ItemStatus.Claimed else ItemStatus.Available
        _claims.update { claims ->
            claims.map {
                if (it.claimId == claimId) it.copy(status = newClaimStatus) else it
            }
        }
        _foundItems.update { items ->
            items.map {
                if (it.itemId == claim.foundItemId) it.copy(status = newItemStatus) else it
            }
        }
        return Result.success(Unit)
    }

    fun getLostItem(id: Int): LostItem? = _lostItems.value.firstOrNull { it.itemId == id }

    fun getFoundItem(id: Int): FoundItem? = _foundItems.value.firstOrNull { it.itemId == id }

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}

package com.example.lost_foundmanagementsystem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.lost_foundmanagementsystem.data.repository.LostFoundRepository

import kotlinx.coroutines.launch

import com.example.lost_foundmanagementsystem.data.models.Claim
import com.example.lost_foundmanagementsystem.data.models.ClaimStatus
import com.example.lost_foundmanagementsystem.data.models.FoundItem
import com.example.lost_foundmanagementsystem.data.models.ItemStatus
import com.example.lost_foundmanagementsystem.data.models.LostItem
import com.example.lost_foundmanagementsystem.data.models.User
import com.example.lost_foundmanagementsystem.data.models.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import com.example.lost_foundmanagementsystem.data.network.dto.ReportLostRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ReportFoundRequest
import com.example.lost_foundmanagementsystem.data.network.dto.ClaimItemRequest
import com.example.lost_foundmanagementsystem.data.network.dto.UpdateClaimRequest
import com.example.lost_foundmanagementsystem.data.network.dto.UpdateFoundItemStatusRequest


data class AppUiState(
    val currentUser: User? = null,
    val lostItems: List<LostItem> = emptyList(),
    val foundItems: List<FoundItem> = emptyList(),
    val claims: List<Claim> = emptyList(),
    val searchQuery: String = "",
    val message: String? = null,
    val error: String? = null
) {
    val filteredLostItems: List<LostItem>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return lostItems
            return lostItems.filter {
                it.itemName.contains(q, ignoreCase = true) ||
                        it.itemDescription.contains(q, ignoreCase = true) ||
                        it.lostLocation.contains(q, ignoreCase = true)
            }
        }

    val filteredFoundItems: List<FoundItem>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return foundItems
            return foundItems.filter {
                it.itemName.contains(q, ignoreCase = true) ||
                        it.itemDescription.contains(q, ignoreCase = true) ||
                        it.foundLocation.contains(q, ignoreCase = true)
            }
        }

    val isStaff: Boolean get() = currentUser?.userType == UserType.Staff
}

class AppViewModel(
    private val repository: LostFoundRepository
): ViewModel() {

    init {
        loadItems()
        loadClaims()
    }

    private fun loadItems() {

        viewModelScope.launch {

            try {

                val lostResponse = repository.getLostItems()
                val foundResponse = repository.getFoundItems()

                if (lostResponse.isSuccessful && lostResponse.body() != null) {

                    _lostItems.value = lostResponse.body()!!.map {
                        LostItem(
                            itemId = it.lost_item_id,
                            itemName = it.item_name,
                            itemDescription = it.item_description,
                            lostLocation = it.lost_location,
                            lostDate = it.lost_date,
                            reportedByUser = it.reported_by,
                            reportedDate = it.reported_date,
                            status = runCatching { ItemStatus.valueOf(it.status) }
                                .getOrDefault(ItemStatus.Open)
                        )
                    }

                }

                if (foundResponse.isSuccessful && foundResponse.body() != null) {

                    _foundItems.value = foundResponse.body()!!.map {
                        FoundItem(
                            itemId = it.found_item_id,
                            itemName = it.item_name,
                            itemDescription = it.item_description,
                            foundLocation = it.found_location,
                            foundDate = it.found_date,
                            foundByUser = it.found_by,
                            reportedDate = it.reported_date,
                            status = runCatching { ItemStatus.valueOf(it.status) }
                                .getOrDefault(ItemStatus.Available)
                        )
                    }

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }

    }

    private fun loadClaims() {

        viewModelScope.launch {

            try {

                val response = repository.getClaims()

                if (response.isSuccessful && response.body() != null) {

                    _claims.value = response.body()!!.map {
                        Claim(
                            claimId = it.claim_id,
                            foundItemId = it.found_item_id,
                            claimantUserId = it.claimant_user_id,
                            claimantName = it.claimant_name,
                            lostLocationAnswer = it.lost_location_answer,
                            lostDateAnswer = it.lost_date_answer,
                            status = when (it.status) {
                                "Approved" -> ClaimStatus.Approved
                                "Rejected" -> ClaimStatus.Rejected
                                else -> ClaimStatus.Pending
                            },
                            submittedAt = it.submitted_at
                        )
                    }
                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }
    }

    private val _currentUser = MutableStateFlow<User?>(null)
    private val _lostItems = MutableStateFlow<List<LostItem>>(emptyList())
    private val _foundItems = MutableStateFlow<List<FoundItem>>(emptyList())
    private val _claims = MutableStateFlow<List<Claim>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _message = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    private val coreState = combine(
        _currentUser,
        _lostItems,
        _foundItems,
        _claims
    ) { user, lost, found, claims ->
        CoreSlice(user, lost, found, claims)
    }

    private val feedbackState = combine(_searchQuery, _message, _error) { query, message, error ->
        FeedbackSlice(query, message, error)
    }

    val uiState: StateFlow<AppUiState> = combine(coreState, feedbackState) { core, feedback ->
        AppUiState(
            currentUser = core.user,
            lostItems = core.lost,
            foundItems = core.found,
            claims = core.claims,
            searchQuery = feedback.query,
            message = feedback.message,
            error = feedback.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppUiState()
    )

    private data class CoreSlice(
        val user: User?,
        val lost: List<LostItem>,
        val found: List<FoundItem>,
        val claims: List<Claim>
    )

    private data class FeedbackSlice(
        val query: String,
        val message: String?,
        val error: String?
    )

    fun login(
        studNumber: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {

        clearFeedback()

        if (studNumber.isBlank() || password.isBlank()) {
            _error.value = "Enter your student number and password."
            onResult(false)
            return
        }

        viewModelScope.launch {

            try {

                val response = repository.login(
                    studNumber.trim(),
                    password
                )

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    val apiUser = response.body()!!.user!!

                    _currentUser.value = User(
                        id = apiUser.user_id,
                        studNumber = apiUser.stud_number,
                        firstName = apiUser.first_name,
                        lastName = apiUser.last_name,
                        password = "",
                        userType = if (apiUser.user_type == "Staff")
                            UserType.Staff
                        else
                            UserType.Student,
                        createdAt = apiUser.created_at
                    )

                    _message.value = response.body()!!.message

                    onResult(true)

                } else {

                    val errorText = response.errorBody()?.string()

                    _error.value =

                        """
        Code: ${response.code()}
        Error: $errorText
        """.trimIndent()

                    onResult(false)

                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unable to connect to server."

                onResult(false)
            }

        }
    }

    fun register(
        studNumber: String,
        firstName: String,
        lastName: String,
        password: String,
        confirmPassword: String,
        asStaff: Boolean,
        onResult: (Boolean) -> Unit
    ) {

        clearFeedback()

        when {

            studNumber.isBlank() ||
                    firstName.isBlank() ||
                    lastName.isBlank() ||
                    password.isBlank() -> {

                _error.value = "Please fill in all fields."
                onResult(false)
                return
            }

            password != confirmPassword -> {

                _error.value = "Passwords do not match."
                onResult(false)
                return
            }

            password.length < 6 -> {

                _error.value = "Password must be at least 6 characters."
                onResult(false)
                return
            }
        }

        viewModelScope.launch {

            try {

                val response = repository.register(
                    studNumber = studNumber.trim(),
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    password = password,
                    userType = if (asStaff) "Staff" else "Student"
                )

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    _message.value = response.body()!!.message

                    onResult(true)

                } else {

                    _error.value =
                        response.body()?.message ?: "Registration failed."

                    onResult(false)
                }

            } catch (e: Exception) {

                _error.value =
                    e.localizedMessage ?: "Unable to connect to server."

                onResult(false)
            }

        }

    }
    fun logout() {
        _currentUser.value = null
        _searchQuery.value = ""
        clearFeedback()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun reportLost(
        itemName: String,
        itemDescription: String,
        lostLocation: String,
        lostDate: String
    ): Boolean {

        clearFeedback()

        val user = _currentUser.value

        if (user == null) {
            _error.value = "Please log in first."
            return false
        }

        if (
            itemName.isBlank() ||
            itemDescription.isBlank() ||
            lostLocation.isBlank() ||
            lostDate.isBlank()
        ) {
            _error.value = "Please complete all fields."
            return false
        }

        viewModelScope.launch {

            try {

                val response = repository.reportLost(
                    ReportLostRequest(
                        user_id = user.id,
                        item_name = itemName,
                        item_description = itemDescription,
                        lost_location = lostLocation,
                        lost_date = lostDate
                    )
                )

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    _message.value = response.body()!!.message

                    loadItems()

                } else {

                    _error.value =
                        response.body()?.message ?: "Failed to report lost item."

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }

        return true
    }

    fun reportFound(
        itemName: String,
        itemDescription: String,
        foundLocation: String,
        foundDate: String
    ): Boolean {
        clearFeedback()
        val user = _currentUser.value
        if (user == null) {
            _error.value = "Please log in first."
            return false
        }
        if (itemName.isBlank() || itemDescription.isBlank() ||
            foundLocation.isBlank() || foundDate.isBlank()
        ) {
            _error.value = "Please complete all fields, including where and when you found it."
            return false
        }
        viewModelScope.launch {

            try {

                val response = repository.reportFound(
                    ReportFoundRequest(
                        user_id = user.id,
                        item_name = itemName,
                        item_description = itemDescription,
                        found_location = foundLocation,
                        found_date = foundDate
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {

                    _message.value = response.body()!!.message

                    loadItems()

                } else {

                    _error.value =
                        response.body()?.message ?: "Unable to report found item."

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }
        return true
    }

    fun submitClaim(
        foundItemId: Int,
        lostLocationAnswer: String,
        lostDateAnswer: String
    ): Boolean {

        clearFeedback()

        val user = _currentUser.value
        if (user == null) {
            _error.value = "Please log in first."
            return false
        }

        if (lostLocationAnswer.isBlank() || lostDateAnswer.isBlank()) {
            _error.value = "Answer both verification questions."
            return false
        }

        viewModelScope.launch {

            try {

                val response = repository.claimItem(
                    ClaimItemRequest(
                        claimant_user_id = user.id,
                        found_item_id = foundItemId,
                        lost_location_answer = lostLocationAnswer,
                        lost_date_answer = lostDateAnswer
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {

                    _message.value = response.body()!!.message

                    loadClaims()

                } else {

                    _error.value =
                        response.body()?.message ?: "Unable to submit claim."

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }

        return true
    }

    fun resolveClaim(claimId: Int, approve: Boolean): Boolean {
        clearFeedback()
        if (_currentUser.value?.userType != UserType.Staff) {
            _error.value = "Only staff can resolve claims."
            return false
        }
        viewModelScope.launch {

            try {

                val response = repository.updateClaim(
                    UpdateClaimRequest(
                        claim_id = claimId,
                        status = if (approve) "Approved" else "Rejected"
                    )
                )

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    _message.value = response.body()!!.message

                    loadClaims()
                    loadItems()

                } else {

                    _error.value =
                        response.body()?.message ?: "Unable to update claim."

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }

        return true
    }

    /**
     * Lets staff directly set a found item's status
     * (e.g. mark it 'Claimed' without needing a pending claim).
     */
    fun updateFoundItemStatus(foundItemId: Int, status: ItemStatus): Boolean {
        clearFeedback()
        if (_currentUser.value?.userType != UserType.Staff) {
            _error.value = "Only staff can change item status."
            return false
        }
        viewModelScope.launch {

            try {

                val response = repository.updateFoundItemStatus(
                    UpdateFoundItemStatusRequest(
                        found_item_id = foundItemId,
                        status = status.name
                    )
                )

                if (response.isSuccessful &&
                    response.body()?.success == true
                ) {

                    _message.value = response.body()!!.message

                    loadItems()

                } else {

                    _error.value =
                        response.body()?.message ?: "Unable to update item status."

                }

            } catch (e: Exception) {

                _error.value = e.localizedMessage

            }

        }

        return true
    }

    fun getLostItem(id: Int): LostItem? =
        _lostItems.value.find { it.itemId == id }

    fun getFoundItem(id: Int): FoundItem? =
        _foundItems.value.find { it.itemId == id }

    fun clearFeedback() {
        _message.value = null
        _error.value = null
    }

    fun consumeMessage() = _message.update { null }

    fun consumeError() = _error.update { null }
}
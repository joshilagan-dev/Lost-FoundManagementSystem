package com.example.lost_foundmanagementsystem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lost_foundmanagementsystem.data.fake.FakeLostFoundRepository
import com.example.lost_foundmanagementsystem.data.models.Claim
import com.example.lost_foundmanagementsystem.data.models.FoundItem
import com.example.lost_foundmanagementsystem.data.models.LostItem
import com.example.lost_foundmanagementsystem.data.models.User
import com.example.lost_foundmanagementsystem.data.models.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

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
    private val repository: FakeLostFoundRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _message = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    private val coreState = combine(
        _currentUser,
        repository.lostItems,
        repository.foundItems,
        repository.claims
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

    fun login(firstName: String, password: String): Boolean {
        clearFeedback()
        if (firstName.isBlank() || password.isBlank()) {
            _error.value = "Enter your first name and password."
            return false
        }
        val user = repository.login(firstName, password)
        return if (user != null) {
            _currentUser.value = user
            _message.value = "Welcome, ${user.fullName}."
            true
        } else {
            _error.value = "Invalid name or password. Try Alex / student123 or Jordan / staff123."
            false
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        password: String,
        confirmPassword: String,
        asStaff: Boolean
    ): Boolean {
        clearFeedback()
        when {
            firstName.isBlank() || lastName.isBlank() || password.isBlank() -> {
                _error.value = "Please fill in all fields."
                return false
            }
            password != confirmPassword -> {
                _error.value = "Passwords do not match."
                return false
            }
            password.length < 6 -> {
                _error.value = "Password must be at least 6 characters."
                return false
            }
        }
        val result = repository.register(
            firstName = firstName,
            lastName = lastName,
            password = password,
            userType = if (asStaff) UserType.Staff else UserType.Student
        )
        return result.fold(
            onSuccess = { user ->
                _currentUser.value = user
                _message.value = "Account created. Welcome, ${user.fullName}."
                true
            },
            onFailure = { e ->
                _error.value = e.message
                false
            }
        )
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
        if (itemName.isBlank() || itemDescription.isBlank() ||
            lostLocation.isBlank() || lostDate.isBlank()
        ) {
            _error.value = "Please complete all fields, including where and when you lost it."
            return false
        }
        repository.addLostItem(
            itemName = itemName,
            itemDescription = itemDescription,
            lostLocation = lostLocation,
            lostDate = lostDate,
            reportedByUser = user.fullName
        )
        _message.value = "Lost item reported."
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
        repository.addFoundItem(
            itemName = itemName,
            itemDescription = itemDescription,
            foundLocation = foundLocation,
            foundDate = foundDate,
            foundByUser = user.fullName
        )
        _message.value = "Found item reported."
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
            _error.value = "Answer both verification questions to confirm ownership."
            return false
        }
        return repository.submitClaim(
            foundItemId = foundItemId,
            claimant = user,
            lostLocationAnswer = lostLocationAnswer,
            lostDateAnswer = lostDateAnswer
        ).fold(
            onSuccess = {
                _message.value = "Claim submitted. Staff will review your answers."
                true
            },
            onFailure = { e ->
                _error.value = e.message
                false
            }
        )
    }

    fun resolveClaim(claimId: Int, approve: Boolean): Boolean {
        clearFeedback()
        if (_currentUser.value?.userType != UserType.Staff) {
            _error.value = "Only staff can resolve claims."
            return false
        }
        return repository.resolveClaim(claimId, approve).fold(
            onSuccess = {
                _message.value = if (approve) "Claim approved." else "Claim rejected."
                true
            },
            onFailure = { e ->
                _error.value = e.message
                false
            }
        )
    }

    fun getLostItem(id: Int): LostItem? = repository.getLostItem(id)

    fun getFoundItem(id: Int): FoundItem? = repository.getFoundItem(id)

    fun clearFeedback() {
        _message.value = null
        _error.value = null
    }

    fun consumeMessage() = _message.update { null }

    fun consumeError() = _error.update { null }
}

class AppViewModelFactory(
    private val repository: FakeLostFoundRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

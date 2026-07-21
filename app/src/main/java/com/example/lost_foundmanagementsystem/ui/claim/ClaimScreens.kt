package com.example.lost_foundmanagementsystem.ui.claim

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lost_foundmanagementsystem.data.models.Claim
import com.example.lost_foundmanagementsystem.data.models.ClaimStatus
import com.example.lost_foundmanagementsystem.data.models.FoundItem
import com.example.lost_foundmanagementsystem.ui.components.ClaimStatusChip
import com.example.lost_foundmanagementsystem.ui.components.DateField
import com.example.lost_foundmanagementsystem.ui.components.FeedbackBanner
import com.example.lost_foundmanagementsystem.ui.components.NavBackIcon
import com.example.lost_foundmanagementsystem.ui.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimVerificationScreen(
    foundItem: FoundItem?,
    error: String?,
    message: String?,
    onClearFeedback: () -> Unit,
    onSubmit: (lostLocationAnswer: String, lostDateAnswer: String) -> Boolean,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var location by rememberSaveable { mutableStateOf("") }
    var lostDate by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify ownership") },
                navigationIcon = { NavBackIcon(onBack) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (foundItem == null) {
                Text("Item not found.")
                return@Column
            }

            SectionTitle("Confirm this item is yours")
            Spacer(modifier = Modifier.height(8.dp))
            FeedbackBanner(message, error, onClearFeedback)

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(foundItem.itemName, fontWeight = FontWeight.SemiBold)
                    Text(foundItem.itemDescription)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Found at ${foundItem.foundLocation} on ${foundItem.foundDate}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Answer these questions so staff can verify you are the rightful owner.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = {
                    Text("Where did you lose this item? (Please specify the room or building.)")
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            DateField(
                label = "On what day/date did you lose it?",
                value = lostDate,
                onDateSelected = { lostDate = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (onSubmit(location, lostDate)) {
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit verification")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffClaimsScreen(
    claims: List<Claim>,
    foundItems: List<FoundItem>,
    error: String?,
    message: String?,
    onClearFeedback: () -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review claims") },
                navigationIcon = { NavBackIcon(onBack) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            FeedbackBanner(message, error, onClearFeedback)
            if (claims.isEmpty()) {
                Text("No claims yet.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(claims, key = { it.claimId }) { claim ->
                        val found = foundItems.firstOrNull { it.itemId == claim.foundItemId }
                        ClaimReviewCard(
                            claim = claim,
                            foundItem = found,
                            onApprove = { onApprove(claim.claimId) },
                            onReject = { onReject(claim.claimId) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ClaimReviewCard(
    claim: Claim,
    foundItem: FoundItem?,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    foundItem?.itemName ?: "Item #${claim.foundItemId}",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                ClaimStatusChip(claim.status)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Claimant: ${claim.claimantName}")
            Text("Submitted: ${claim.submittedAt}")
            Spacer(modifier = Modifier.height(10.dp))
            Text("Verification answers", fontWeight = FontWeight.SemiBold)
            Text("Where lost: ${claim.lostLocationAnswer}")
            Text("Date lost: ${claim.lostDateAnswer}")
            if (foundItem != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Item found at ${foundItem.foundLocation} on ${foundItem.foundDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (claim.status == ClaimStatus.Pending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f)
                    ) { Text("Approve") }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f)
                    ) { Text("Reject") }
                }
            }
        }
    }
}

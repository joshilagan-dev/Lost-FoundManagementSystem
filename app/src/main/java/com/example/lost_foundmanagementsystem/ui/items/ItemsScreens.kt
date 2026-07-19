package com.example.lost_foundmanagementsystem.ui.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lost_foundmanagementsystem.data.models.FoundItem
import com.example.lost_foundmanagementsystem.data.models.ItemStatus
import com.example.lost_foundmanagementsystem.data.models.LostItem
import com.example.lost_foundmanagementsystem.ui.components.ItemCard
import com.example.lost_foundmanagementsystem.ui.components.NavBackIcon
import com.example.lost_foundmanagementsystem.ui.components.SectionTitle
import com.example.lost_foundmanagementsystem.ui.components.StatusChip
import com.example.lost_foundmanagementsystem.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    lostItems: List<LostItem>,
    foundItems: List<FoundItem>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onOpenLost: (Int) -> Unit,
    onOpenFound: (Int) -> Unit,
    onBack: () -> Unit
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Lost", "Found")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse Items") },
                navigationIcon = { NavBackIcon(onBack) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search by name, description, or place") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )
            TabRow(selectedTabIndex = tabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when (tabIndex) {
                0 -> {
                    if (lostItems.isEmpty()) {
                        EmptyState("No lost items match your search.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(lostItems, key = { it.itemId }) { item ->
                                ItemCard(
                                    title = item.itemName,
                                    subtitle = item.itemDescription,
                                    meta = "Lost at ${item.lostLocation} · ${item.lostDate}",
                                    status = item.status,
                                    onClick = { onOpenLost(item.itemId) }
                                )
                            }
                        }
                    }
                }
                else -> {
                    if (foundItems.isEmpty()) {
                        EmptyState("No found items match your search.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(foundItems, key = { it.itemId }) { item ->
                                ItemCard(
                                    title = item.itemName,
                                    subtitle = item.itemDescription,
                                    meta = "Found at ${item.foundLocation} · ${item.foundDate}",
                                    status = item.status,
                                    onClick = { onOpenFound(item.itemId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    type: String,
    lostItem: LostItem?,
    foundItem: FoundItem?,
    canClaim: Boolean,
    onClaim: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (type == Routes.TYPE_LOST) "Lost item" else "Found item"
                    )
                },
                navigationIcon = { NavBackIcon(onBack) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (type) {
                Routes.TYPE_LOST -> {
                    if (lostItem == null) {
                        Text("Item not found.")
                    } else {
                        DetailBlock(
                            name = lostItem.itemName,
                            description = lostItem.itemDescription,
                            placeLabel = "Where it was lost",
                            place = lostItem.lostLocation,
                            dateLabel = "Date lost",
                            date = lostItem.lostDate,
                            reporter = "Reported by ${lostItem.reportedByUser} on ${lostItem.reportedDate}",
                            status = lostItem.status
                        )
                    }
                }
                else -> {
                    if (foundItem == null) {
                        Text("Item not found.")
                    } else {
                        DetailBlock(
                            name = foundItem.itemName,
                            description = foundItem.itemDescription,
                            placeLabel = "Where it was found",
                            place = foundItem.foundLocation,
                            dateLabel = "Date found",
                            date = foundItem.foundDate,
                            reporter = "Found by ${foundItem.foundByUser} on ${foundItem.reportedDate}",
                            status = foundItem.status
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        if (canClaim && foundItem.status == ItemStatus.Available) {
                            Button(
                                onClick = onClaim,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("This is mine — verify ownership")
                            }
                        } else if (foundItem.status == ItemStatus.ClaimPending) {
                            Text("A claim is pending staff review.")
                        } else if (foundItem.status == ItemStatus.Claimed) {
                            Text("This item has already been claimed.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailBlock(
    name: String,
    description: String,
    placeLabel: String,
    place: String,
    dateLabel: String,
    date: String,
    reporter: String,
    status: ItemStatus
) {
    SectionTitle(name)
    Spacer(modifier = Modifier.height(8.dp))
    StatusChip(status)
    Spacer(modifier = Modifier.height(12.dp))
    Text(description, style = MaterialTheme.typography.bodyLarge)
    Spacer(modifier = Modifier.height(16.dp))
    Text(placeLabel, fontWeight = FontWeight.SemiBold)
    Text(place)
    Spacer(modifier = Modifier.height(8.dp))
    Text(dateLabel, fontWeight = FontWeight.SemiBold)
    Text(date)
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        reporter,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

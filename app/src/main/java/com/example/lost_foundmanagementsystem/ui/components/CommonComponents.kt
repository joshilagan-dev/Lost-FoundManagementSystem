package com.example.lost_foundmanagementsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lost_foundmanagementsystem.data.models.ClaimStatus
import com.example.lost_foundmanagementsystem.data.models.ItemStatus
import com.example.lost_foundmanagementsystem.ui.theme.StatusAmber
import com.example.lost_foundmanagementsystem.ui.theme.StatusGreen
import com.example.lost_foundmanagementsystem.ui.theme.StatusRed
import com.example.lost_foundmanagementsystem.ui.theme.Purple80
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusChip(status: ItemStatus) {
    val (label, color) = when (status) {
        ItemStatus.Open, ItemStatus.Available -> "Available" to StatusGreen
        ItemStatus.ClaimPending -> "Claim pending" to StatusAmber
        ItemStatus.Claimed -> "Claimed" to MaterialTheme.colorScheme.primary
        ItemStatus.Matched -> "Matched" to MaterialTheme.colorScheme.secondary
        ItemStatus.Closed -> "Closed" to StatusRed
    }
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = color.copy(alpha = 0.15f),
            disabledLabelColor = color
        )
    )
}

@Composable
fun ClaimStatusChip(status: ClaimStatus) {
    val (label, color) = when (status) {
        ClaimStatus.Pending -> "Pending" to StatusAmber
        ClaimStatus.Approved -> "Approved" to StatusGreen
        ClaimStatus.Rejected -> "Rejected" to StatusRed
        ClaimStatus.Claimed -> "Claimed" to Purple80
    }
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(label) },
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = color.copy(alpha = 0.15f),
            disabledLabelColor = color
        )
    )
}

@Composable
fun ItemCard(
    title: String,
    subtitle: String,
    meta: String,
    status: ItemStatus,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(status)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = meta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }

    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            TextButton(onClick = { showPicker = true }) {
                Text("Pick")
            }
        }
    )

    if (showPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.selectedDateMillis?.let { millis ->
                            onDateSelected(formatter.format(Date(millis)))
                        }
                        showPicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun NavBackIcon(onBack: () -> Unit) {
    TextButton(onClick = onBack) {
        Text("Back")
    }
}

@Composable
fun FeedbackBanner(message: String?, error: String?, onDismiss: () -> Unit) {
    val text = error ?: message
    val color = if (error != null) StatusRed else StatusGreen
    if (text != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
            onClick = onDismiss
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(12.dp),
                color = color,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

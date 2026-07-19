package com.example.lost_foundmanagementsystem.ui.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.example.lost_foundmanagementsystem.ui.components.DateField
import com.example.lost_foundmanagementsystem.ui.components.FeedbackBanner
import com.example.lost_foundmanagementsystem.ui.components.NavBackIcon
import com.example.lost_foundmanagementsystem.ui.components.SectionTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostScreen(
    error: String?,
    message: String?,
    onClearFeedback: () -> Unit,
    onSubmit: (
        itemName: String,
        itemDescription: String,
        lostLocation: String,
        lostDate: String
    ) -> Boolean,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var itemName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var lostDate by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Lost Item") },
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
            SectionTitle("Tell us what you lost")
            Spacer(modifier = Modifier.height(8.dp))
            FeedbackBanner(message, error, onClearFeedback)

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Verification details")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "These answers help confirm you are the rightful owner if a match is found."
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = {
                    Text("Where did you lose this item? (room or building)")
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
                    if (onSubmit(itemName, description, location, lostDate)) {
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit lost report")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFoundScreen(
    error: String?,
    message: String?,
    onClearFeedback: () -> Unit,
    onSubmit: (
        itemName: String,
        itemDescription: String,
        foundLocation: String,
        foundDate: String
    ) -> Boolean,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var itemName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var foundDate by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Found Item") },
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
            SectionTitle("What did you find?")
            Spacer(modifier = Modifier.height(8.dp))
            FeedbackBanner(message, error, onClearFeedback)

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Where did you find it? (room or building)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            DateField(
                label = "On what day/date did you find it?",
                value = foundDate,
                onDateSelected = { foundDate = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (onSubmit(itemName, description, location, foundDate)) {
                        onSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit found report")
            }
        }
    }
}

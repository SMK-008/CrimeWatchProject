package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.crimewatch.data.models.CrimeReport
import com.example.crimewatch.data.models.CrimeUpdate
import com.example.crimewatch.viewmodel.AuthViewModel
import com.example.crimewatch.viewmodel.CrimeReportViewModel
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CrimeDetailScreen(
    reportId: String?,
    crimeReportViewModel: CrimeReportViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var updateText by remember { mutableStateOf("") }
    var report by remember { mutableStateOf<CrimeReport?>(null) }
    var updates by remember { mutableStateOf<List<CrimeUpdate>>(emptyList()) }
    val authState by authViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()

    // Load report and updates
    LaunchedEffect(reportId) {
        if (reportId != null) {
            updates = crimeReportViewModel.getUpdatesForReport(reportId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        report?.let { crimeReport ->
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Images
                if (crimeReport.imageUrls.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(crimeReport.imageUrls) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Crime Scene Image",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillParentMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Report details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = crimeReport.headline,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = crimeReport.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Divider()
                            Text(
                                text = "Location: ${crimeReport.location}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Crime Type: ${crimeReport.crimeType}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            crimeReport.suspectDescription?.let { description ->
                                Text(
                                    text = "Suspect Description: $description",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                text = "Reported by: ${crimeReport.reporterName}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Status: ${crimeReport.status}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            crimeReport.timestamp?.toDate()?.let { date ->
                                Text(
                                    text = "Reported on: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                // Updates
                item {
                    Text(
                        text = "Updates",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(updates) { update ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = update.message,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "By: ${update.userName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                update.timestamp?.toDate()?.let { date ->
                                    Text(
                                        text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add update section
            if (authState.user != null) {
                OutlinedTextField(
                    value = updateText,
                    onValueChange = { updateText = it },
                    label = { Text("Add an update") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (updateText.isNotBlank() && reportId != null) {
                                scope.launch {
                                    crimeReportViewModel.addUpdate(
                                        crimeReportId = reportId,
                                        message = updateText,
                                        userId = authState.user?.uid ?: "",
                                        userName = authState.user?.displayName ?: "Anonymous"
                                    )
                                    updateText = ""
                                }
                            }
                        }
                    )
                )
            }
        } ?: run {
            // Loading or error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.crimewatch.data.models.CrimeReport
import com.example.crimewatch.data.models.CrimeUpdate
import com.example.crimewatch.viewmodel.AuthViewModel
import com.example.crimewatch.viewmodel.CrimeReportViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeDetailScreen(
    reportId: String?,
    onNavigateBack: () -> Unit,
    crimeReportViewModel: CrimeReportViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var updateText by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    val report by crimeReportViewModel.selectedReport.collectAsState()
    val updates by crimeReportViewModel.updates.collectAsState()

    // Load report and updates
    LaunchedEffect(reportId) {
        if (reportId != null) {
            crimeReportViewModel.loadCrimeReport(reportId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crime Report Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (authState.user?.uid == report?.reportedBy) {
                        IconButton(onClick = {
                            scope.launch {
                                crimeReportViewModel.updateReportStatus(
                                    reportId ?: return@launch,
                                    when (report?.status) {
                                        "PENDING" -> "INVESTIGATING"
                                        "INVESTIGATING" -> "RESOLVED"
                                        "RESOLVED" -> "CLOSED"
                                        else -> "PENDING"
                                    }
                                )
                            }
                        }) {
                            Icon(Icons.Default.Edit, "Update Status")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (report == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Images
            if (report?.imageUrls?.isNotEmpty() == true) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(report?.imageUrls ?: emptyList()) { imageUrl ->
                            Card(
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillParentMaxWidth()
                            ) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Crime scene image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }

            // Status Chip
            item {
                Surface(
                    color = when (report?.status) {
                        "PENDING" -> MaterialTheme.colorScheme.errorContainer
                        "INVESTIGATING" -> MaterialTheme.colorScheme.primaryContainer
                        "RESOLVED" -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = report?.status ?: "",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            // Main Content
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = report?.headline ?: "",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Type: ${report?.crimeType}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        report?.timestamp?.toDate()?.let { date ->
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        text = report?.description ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = report?.location ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    report?.suspectDescription?.let { description ->
                        if (description.isNotBlank()) {
                            Text(
                                text = "Suspect Description:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Updates Section
            item {
                Text(
                    text = "Updates",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // Add Update
            item {
                if (authState.user != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = updateText,
                            onValueChange = { updateText = it },
                            placeholder = { Text("Add an update...") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (updateText.isNotBlank() && reportId != null) {
                                    scope.launch {
                                        crimeReportViewModel.addUpdate(
                                            reportId,
                                            updateText
                                        )
                                        updateText = ""
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, "Send update")
                        }
                    }
                }
            }

            // Updates List
            items(updates) { update ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = update.userName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            update.timestamp?.toDate()?.let { date ->
                                Text(
                                    text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = update.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

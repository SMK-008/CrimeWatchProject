package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.crimewatch.viewmodel.MissingPersonViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissingPersonDetailScreen(
    personId: String?,
    viewModel: MissingPersonViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var updateText by remember { mutableStateOf("") }
    val person by viewModel.selectedPerson.collectAsState()
    val updates by viewModel.updates.collectAsState()
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(personId) {
        if (personId != null) {
            viewModel.loadMissingPerson(personId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Missing Person Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            person?.let { missingPerson ->
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Images
                    if (missingPerson.imageUrls.isNotEmpty()) {
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(missingPerson.imageUrls) { imageUrl ->
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Missing person image",
                                        modifier = Modifier
                                            .height(200.dp)
                                            .fillParentMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    // Person details
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
                                    text = missingPerson.name,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    text = "Age: ${missingPerson.age}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Divider()
                                Text(
                                    text = "Last Seen: ${missingPerson.lastSeenLocation}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Last Seen Date: ${missingPerson.lastSeenDate}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Description: ${missingPerson.description}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Contact Information: ${missingPerson.contactInfo}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Status: ${missingPerson.status}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Reported by: ${missingPerson.reporterName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                missingPerson.timestamp?.toDate()?.let { date ->
                                    Text(
                                        text = "Reported on: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // Updates section
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

                // Add update section with error handling
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    OutlinedTextField(
                        value = updateText,
                        onValueChange = { 
                            updateText = it
                            errorMessage = null
                        },
                        label = { Text("Add an update") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSubmitting,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (updateText.isNotBlank() && personId != null) {
                                    scope.launch {
                                        isSubmitting = true
                                        try {
                                            viewModel.addUpdate(
                                                missingPersonId = personId,
                                                message = updateText
                                            )
                                            updateText = ""
                                            errorMessage = null
                                        } catch (e: Exception) {
                                            errorMessage = "Failed to add update. Please try again."
                                        } finally {
                                            isSubmitting = false
                                        }
                                    }
                                } else if (updateText.isBlank()) {
                                    errorMessage = "Update message cannot be empty"
                                }
                            }
                        )
                    )
                    
                    Button(
                        onClick = {
                            if (updateText.isNotBlank() && personId != null) {
                                scope.launch {
                                    isSubmitting = true
                                    try {
                                        viewModel.addUpdate(
                                            missingPersonId = personId,
                                            message = updateText
                                        )
                                        updateText = ""
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to add update. Please try again."
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            } else if (updateText.isBlank()) {
                                errorMessage = "Update message cannot be empty"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = !isSubmitting && updateText.isNotBlank()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Submit Update")
                        }
                    }
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
}

package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CrimeDetailScreen(reportId: String?) {
    // Mock data; you would fetch this dynamically based on reportId in a real app
    val report = when (reportId) {
        "1" -> CrimeReport(
            id = "1",
            headline = "Robbery at Local Mall",
            description = "A suspect robbed a jewelry store at the central mall. Witnesses describe them as wearing a black hoodie.",
            suspectName = "John Doe",
            crimeCommitted = "Armed Robbery",
            imageUrl = "https://via.placeholder.com/150"
        )
        "2" -> CrimeReport(
            id = "2",
            headline = "Vandalism in Downtown Area",
            description = "Graffiti found on multiple public walls. Suspect reportedly seen with a spray can.",
            suspectName = "Jane Smith",
            crimeCommitted = "Vandalism",
            imageUrl = "https://via.placeholder.com/150"
        )
        else -> null
    }

    var updateText by remember { mutableStateOf("") }
    val updates = remember {
        mutableStateListOf(
            Update(user = "Witness A", message = "I saw someone matching this description near the mall yesterday.", timestamp = "2024-11-25 10:30 AM")
        )
    }

    if (report != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Crime Details
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = report.imageUrl,
                        contentDescription = "Suspect Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = report.headline, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "Suspect Name: ${report.suspectName}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Crime Committed: ${report.crimeCommitted}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // Updates Section
            Text(
                text = "Updates on the Crime",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(updates.size) { index ->
                    val update = updates[index]
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = update.message,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "By: ${update.user}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = update.timestamp,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Add Update Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = updateText,
                    onValueChange = { updateText = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Add an update") },
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        postUpdate(updateText, updates)
                        updateText = ""
                    })
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        postUpdate(updateText, updates)
                        updateText = ""
                    },
                    enabled = updateText.isNotBlank()
                ) {
                    Text("Post")
                }
            }
        }
    } else {
        Text(
            text = "Report not found.",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}

private fun postUpdate(updateText: String, updates: MutableList<Update>) {
    if (updateText.isNotBlank()) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())
        updates.add(Update(user = "Anonymous", message = updateText, timestamp = currentTime))
    }
}

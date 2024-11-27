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
import com.example.crimewatch.data.models.*

data class Update(
    val user: String,
    val message: String,
    val timestamp: String
)

@Composable
fun MissingPersonDetailScreen(personId: String?) {
    // Mock data; in a real app, this data should be fetched dynamically using personId
    val person = when (personId) {
        "1" -> MissingPerson(
            id = "1",
            name = "Alice Johnson",
            age = 25,
            lastSeenLocation = "Central Park, NY",
            description = "Alice was last seen wearing a blue jacket and jeans.",
            imageUrl = "https://via.placeholder.com/150"
        )
        "2" -> MissingPerson(
            id = "2",
            name = "Michael Smith",
            age = 40,
            lastSeenLocation = "Downtown LA",
            description = "Michael was last seen at a coffee shop near Main Street.",
            imageUrl = "https://via.placeholder.com/150"
        )
        else -> null
    }

    var updateText by remember { mutableStateOf("") }
    val updates = remember {
        mutableStateListOf(
            Update(
                user = "John Doe",
                message = "Last seen near Central Park on Nov 20.",
                timestamp = "2024-11-25 10:30 AM"
            )
        )
    }

    if (person != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = person.imageUrl,
                        contentDescription = "Missing Person Image",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = person.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "Age: ${person.age}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Last Seen Location: ${person.lastSeenLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = person.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Updates on ${person.name}'s Whereabouts",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
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
            text = "Missing person not found.",
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

package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

data class CommunityUpdate(
    val user: String,
    val message: String,
    val timestamp: String
)

@Composable
fun CommunityTipDetailScreen(tipId: String?) {
    // Mock data - fetch dynamically in a real app
    val tip = when (tipId) {
        "1" -> CommunityTip("1", "Report Suspicious Activity", "Always report any suspicious behavior or activity to the authorities. Provide detailed descriptions when possible.")
        "2" -> CommunityTip("2", "Ensure Public Spaces Are Well-Lit", "Good lighting in public areas can help deter crime and provide a sense of security for residents.")
        "3" -> CommunityTip("3", "Be Cautious of Strangers", "Teach children to avoid interacting with strangers without adult supervision. Encourage them to seek help from a trusted adult in case of an emergency.")
        else -> null
    }

    var updateText by remember { mutableStateOf("") }
    val updates = remember {
        mutableStateListOf(
            CommunityUpdate(user = "Anonymous", message = "Great tip! More communities should adopt this practice.", timestamp = "2024-11-25 12:00 PM")
        )
    }

    if (tip != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = tip.title,
                style = TextStyle(fontSize = MaterialTheme.typography.headlineMedium.fontSize, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = tip.details,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Community Responses",
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
                    label = { Text("Add a response") },
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
            text = "Tip not found.",
            modifier = Modifier.fillMaxSize().padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

private fun postUpdate(updateText: String, updates: MutableList<CommunityUpdate>) {
    if (updateText.isNotBlank()) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())
        updates.add(CommunityUpdate(user = "Anonymous", message = updateText, timestamp = currentTime))
    }
}

package com.example.crimewatch.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.crimewatch.data.models.MissingPerson
import com.example.crimewatch.viewmodel.MissingPersonViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissingPersonsScreen(
    navController: NavHostController,
    viewModel: MissingPersonViewModel = viewModel()
) {
    var showReportDialog by remember { mutableStateOf(false) }
    val missingPersons by viewModel.missingPersons.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Missing Persons",
                style = MaterialTheme.typography.headlineSmall
            )
            FloatingActionButton(
                onClick = { showReportDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, "Report Missing Person")
            }
        }

        Text(
            text = "Help find missing individuals in your area. Click on a name to learn more and assist in the search.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(missingPersons) { person ->
                MissingPersonItem(
                    person = person,
                    onClick = { navController.navigate("missing_person_detail/${person.id}") }
                )
            }
        }
    }

    if (showReportDialog) {
        ReportMissingPersonDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { name, age, description, lastSeen, lastSeenDate, contact, images, lat, long ->
                scope.launch {
                    viewModel.reportMissingPerson(
                        name = name,
                        age = age,
                        description = description,
                        lastSeenLocation = lastSeen,
                        lastSeenDate = lastSeenDate,
                        contactInfo = contact,
                        images = images,
                        latitude = lat,
                        longitude = long
                    )
                    showReportDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportMissingPersonDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String, String, String, String, List<Uri>, Double?, Double?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lastSeen by remember { mutableStateOf("") }
    var lastSeenDate by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        images = uris
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Missing Person") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                OutlinedTextField(
                    value = lastSeen,
                    onValueChange = { lastSeen = it },
                    label = { Text("Last Seen Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = lastSeenDate,
                    onValueChange = { lastSeenDate = it },
                    label = { Text("Last Seen Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Information") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photos")
                }

                if (images.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(images) { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected image",
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && age.isNotBlank()) {
                        onSubmit(
                            name,
                            age.toIntOrNull() ?: 0,
                            description,
                            lastSeen,
                            lastSeenDate,
                            contact,
                            images,
                            null, // latitude
                            null  // longitude
                        )
                    }
                }
            ) {
                Text("Submit Report")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissingPersonItem(person: MissingPerson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (person.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = person.imageUrls.first(),
                    contentDescription = "Missing person image",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Age: ${person.age}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Last seen: ${person.lastSeenLocation}",
                    style = MaterialTheme.typography.bodySmall
                )
                person.timestamp?.toDate()?.let { date ->
                    Text(
                        text = "Reported: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

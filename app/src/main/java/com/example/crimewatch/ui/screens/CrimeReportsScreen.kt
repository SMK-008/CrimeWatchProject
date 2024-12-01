package com.example.crimewatch.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.crimewatch.data.models.CrimeReport
import com.example.crimewatch.viewmodel.AuthViewModel
import com.example.crimewatch.viewmodel.CrimeReportViewModel
import com.example.crimewatch.viewmodel.CrimeReportResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrimeReportsScreen(
    navController: NavHostController,
    crimeReportViewModel: CrimeReportViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var showAddReport by remember { mutableStateOf(false) }
    val reportsState by crimeReportViewModel.reportsState.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with Add Report button
        SmallTopAppBar(
            title = { Text("Crime Reports") },
            actions = {
                IconButton(onClick = { showAddReport = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Report")
                }
            }
        )

        if (reportsState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reportsState.reports) { report ->
                    CrimeReportCard(
                        report = report,
                        onClick = { navController.navigate("crime_detail/${report.id}") }
                    )
                }
            }
        }
    }

    if (showAddReport) {
        AddReportDialog(
            onDismiss = { showAddReport = false },
            onSubmit = { report ->
                showAddReport = false
                // Handle report submission
            },
            userId = authState.user?.uid ?: "",
            userName = authState.user?.displayName ?: "Anonymous"
        )
    }
}

@Composable
fun CrimeReportCard(report: CrimeReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Display first image if available
            if (report.imageUrls.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(report.imageUrls.first()),
                    contentDescription = "Crime Scene",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = report.headline,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.location,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = report.timestamp?.toDate()?.let { 
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)
                    } ?: "Unknown date",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReportDialog(
    onDismiss: () -> Unit,
    onSubmit: (CrimeReport) -> Unit,
    userId: String,
    userName: String,
    crimeReportViewModel: CrimeReportViewModel = viewModel()
) {
    var headline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var crimeType by remember { mutableStateOf("") }
    var suspectDescription by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages = uris
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report a Crime") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Headline") },
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
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = crimeType,
                    onValueChange = { crimeType = it },
                    label = { Text("Crime Type") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = suspectDescription,
                    onValueChange = { suspectDescription = it },
                    label = { Text("Suspect Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Image selection
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Images")
                }

                if (selectedImages.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImages) { uri ->
                            Box {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(4.dp)
                                )
                                IconButton(
                                    onClick = {
                                        selectedImages = selectedImages.filter { it != uri }
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove image"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        val result = crimeReportViewModel.submitCrimeReport(
                            headline = headline,
                            description = description,
                            location = location,
                            crimeType = crimeType,
                            suspectDescription = suspectDescription.takeIf { it.isNotBlank() },
                            images = selectedImages,
                            userId = userId,
                            userName = userName,
                            latitude = null, // TODO: Implement location
                            longitude = null
                        )
                        isSubmitting = false
                        when (result) {
                            is CrimeReportResult.Success -> onDismiss()
                            is CrimeReportResult.Error -> {
                                // Show error message
                            }
                            CrimeReportResult.Loading -> {}
                        }
                    }
                },
                enabled = !isSubmitting && headline.isNotBlank() && description.isNotBlank() && 
                         location.isNotBlank() && crimeType.isNotBlank()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Report")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

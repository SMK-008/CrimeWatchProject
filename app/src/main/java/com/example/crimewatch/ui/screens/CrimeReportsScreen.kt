package com.example.crimewatch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

data class CrimeReport(
    val id: String,
    val headline: String,
    val description: String,
    val suspectName: String,
    val crimeCommitted: String,
    val imageUrl: String
)

@Composable
fun CrimeReportsScreen(navController: NavHostController) {
    val reports = listOf(
        CrimeReport(
            id = "1",
            headline = "Robbery at Local Mall",
            description = "A suspect robbed a jewelry store at the central mall. Witnesses describe them as wearing a black hoodie.",
            suspectName = "John Doe",
            crimeCommitted = "Armed Robbery",
            imageUrl = "https://via.placeholder.com/150"
        ),
        CrimeReport(
            id = "2",
            headline = "Vandalism in Downtown Area",
            description = "Graffiti found on multiple public walls. Suspect reportedly seen with a spray can.",
            suspectName = "Jane Smith",
            crimeCommitted = "Vandalism",
            imageUrl = "https://via.placeholder.com/150"
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Crime Reports",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Stay informed about recent criminal activities in your community. Click on a report for more details.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reports.size) { index ->
                val report = reports[index]
                CrimeReportItem(report = report) {
                    navController.navigate("crime_detail/${report.id}")
                }
            }
        }
    }
}

@Composable
fun CrimeReportItem(report: CrimeReport, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(data = report.imageUrl),
                contentDescription = "Crime Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = report.headline,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = report.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

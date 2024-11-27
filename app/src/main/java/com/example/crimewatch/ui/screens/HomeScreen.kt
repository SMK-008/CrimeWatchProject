//package com.example.crimewatch.ui.screens
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
////import com.example.crimewatch.data.models.CrimeReport
//
////data class CrimeReport(val title: String, val description: String)
//
//@Composable
//fun HomeScreen(reports: List<CrimeReport>) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text(
//            text = "Crime Reports",
//            style = MaterialTheme.typography.headlineMedium,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//        LazyColumn {
//            items(reports) { report ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(bottom = 8.dp),
//                    elevation = CardDefaults.cardElevation(4.dp)
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
////                        Text(text = report.title, style = MaterialTheme.typography.titleLarge)
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text(text = report.description, style = MaterialTheme.typography.bodyMedium)
//                    }
//                }
//            }
//        }
//    }
//}

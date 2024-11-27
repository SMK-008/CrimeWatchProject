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
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.crimewatch.data.models.MissingPerson

@Composable
fun MissingPersonsScreen(navController: NavHostController) {
    val missingPersons = listOf(
        MissingPerson(
            id = "1",
            name = "Alice Johnson",
            age = 25,
            lastSeenLocation = "Central Park, NY",
            description = "Alice was last seen wearing a blue jacket and jeans.",
            imageUrl = "https://via.placeholder.com/150"
        ),
        MissingPerson(
            id = "2",
            name = "Michael Smith",
            age = 40,
            lastSeenLocation = "Downtown LA",
            description = "Michael was last seen at a coffee shop near Main Street.",
            imageUrl = "https://via.placeholder.com/150"
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Missing Persons",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Help find missing individuals in your area. Click on a name to learn more and assist in the search.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(missingPersons.size) { index ->
                val person = missingPersons[index]
                MissingPersonItem(person = person) {
                    navController.navigate("missing_person_detail/${person.id}")
                }
            }
        }
    }
}

@Composable
fun MissingPersonItem(person: MissingPerson, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberImagePainter(data = person.imageUrl),
                contentDescription = "Person Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = person.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Last seen at: ${person.lastSeenLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = person.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

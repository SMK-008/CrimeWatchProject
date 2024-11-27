package com.example.crimewatch.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

data class CommunityTip(
    val id: String,
    val title: String,
    val details: String
)

@Composable
fun CommunityScreen(navController: NavHostController) {
    val tips = listOf(
        CommunityTip("1", "Report Suspicious Activity", "Always report any suspicious behavior or activity to the authorities."),
        CommunityTip("2", "Ensure Public Spaces Are Well-Lit", "Good lighting in public areas helps deter crime."),
        CommunityTip("3", "Be Cautious of Strangers", "Teach children to avoid interacting with strangers without adult supervision.")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Community Safety Tips",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tips.size) { index ->
                val tip = tips[index]
                CommunityTipItem(tip = tip) {
                    navController.navigate("community_tip_detail/${tip.id}")
                }
            }
        }
    }
}

@Composable
fun CommunityTipItem(tip: CommunityTip, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tip.details,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

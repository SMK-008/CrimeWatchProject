package com.example.crimewatch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.crimewatch.data.models.CommunityTipComment
import com.example.crimewatch.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTipDetailScreen(
    tipId: String,
    onNavigateBack: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val selectedTip by viewModel.selectedTip.collectAsState()
    val comments by viewModel.comments.collectAsState()
    var newComment by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(tipId) {
        viewModel.loadTip(tipId)
        viewModel.incrementViews(tipId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Community Tip") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        selectedTip?.let { tip ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Images
                if (tip.imageUrls.isNotEmpty()) {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tip.imageUrls) { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Tip image",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillParentMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Title and Category
                item {
                    Column {
                        Text(
                            text = tip.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Description
                item {
                    Text(
                        text = tip.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Location and Date
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tip.location,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        tip.timestamp?.toDate()?.let { date ->
                            Text(
                                text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Stats
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        viewModel.likeTip(tip.id)
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Favorite,
                                    contentDescription = "Like",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = "${tip.likes}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.RemoveRedEye,
                                contentDescription = "Views"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${tip.views}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                // Comments Section
                item {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Add Comment
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            placeholder = { Text("Add a comment...") },
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (newComment.isNotBlank()) {
                                    scope.launch {
                                        viewModel.addComment(tipId, newComment)
                                        newComment = ""
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, "Send comment")
                        }
                    }
                }

                // Comments List
                items(comments) { comment ->
                    CommentItem(comment = comment)
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommunityTipComment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                comment.timestamp?.toDate()?.let { date ->
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = comment.message,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Likes",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${comment.likes}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

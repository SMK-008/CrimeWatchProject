package com.example.crimewatch.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class CommunityTip(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "",
    val imageUrls: List<String> = emptyList(),
    val reportedBy: String = "", // User ID
    val reporterName: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val status: String = "ACTIVE", // ACTIVE, RESOLVED, ARCHIVED
    val latitude: Double? = null,
    val longitude: Double? = null,
    val likes: Int = 0,
    val views: Int = 0
)

data class CommunityTipComment(
    @DocumentId
    val id: String = "",
    val tipId: String = "",
    val message: String = "",
    val userId: String = "",
    val userName: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val likes: Int = 0
)

package com.example.crimewatch.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class CrimeReport(
    @DocumentId
    val id: String = "",
    val headline: String = "",
    val description: String = "",
    val location: String = "",
    val crimeType: String = "",
    val suspectDescription: String? = null,
    val imageUrls: List<String> = emptyList(),
    val reportedBy: String = "", // User ID who reported
    val reporterName: String = "", // Name of the reporter
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val status: String = "PENDING", // PENDING, INVESTIGATING, RESOLVED, CLOSED
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class CrimeUpdate(
    @DocumentId
    val id: String = "",
    val crimeReportId: String = "",
    val message: String = "",
    val userId: String = "",
    val userName: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)

package com.example.crimewatch.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class MissingPerson(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val description: String = "",
    val lastSeenLocation: String = "",
    val imageUrls: List<String> = emptyList(),
    val reportedBy: String = "", // User ID who reported
    val reporterName: String = "", // Name of the reporter
    @ServerTimestamp
    val timestamp: Timestamp? = null,
    val status: String = "MISSING", // MISSING, FOUND, INVESTIGATION_ONGOING
    val lastSeenDate: String = "",
    val contactInfo: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class MissingPersonUpdate(
    @DocumentId
    val id: String = "",
    val missingPersonId: String = "",
    val message: String = "",
    val userId: String = "",
    val userName: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)

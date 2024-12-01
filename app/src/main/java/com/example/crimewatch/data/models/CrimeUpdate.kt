package com.example.crimewatch.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class CrimeUpdate(
    val crimeReportId: String = "",
    val message: String = "",
    val userId: String = "",
    val userName: String = "",
    @ServerTimestamp
    val timestamp: Timestamp? = null
)

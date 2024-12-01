package com.example.crimewatch.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.example.crimewatch.data.models.CrimeReport
import com.example.crimewatch.data.models.CrimeUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

sealed class CrimeReportResult {
    object Success : CrimeReportResult()
    data class Error(val message: String) : CrimeReportResult()
    object Loading : CrimeReportResult()
}

data class CrimeReportsState(
    val isLoading: Boolean = false,
    val reports: List<CrimeReport> = emptyList(),
    val error: String? = null
)

class CrimeReportViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    private val _reportsState = MutableStateFlow(CrimeReportsState())
    val reportsState: StateFlow<CrimeReportsState> = _reportsState

    init {
        loadCrimeReports()
    }

    private fun loadCrimeReports() {
        viewModelScope.launch {
            try {
                _reportsState.value = _reportsState.value.copy(isLoading = true)
                
                val snapshot = firestore.collection("crime_reports")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val reports = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CrimeReport::class.java)
                }
                
                _reportsState.value = _reportsState.value.copy(
                    isLoading = false,
                    reports = reports
                )
            } catch (e: Exception) {
                _reportsState.value = _reportsState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    suspend fun uploadImage(uri: Uri): String {
        val imageRef = storage.reference.child("crime_reports/${UUID.randomUUID()}")
        val uploadTask = imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }

    suspend fun submitCrimeReport(
        headline: String,
        description: String,
        location: String,
        crimeType: String,
        suspectDescription: String?,
        images: List<Uri>,
        userId: String,
        userName: String,
        latitude: Double?,
        longitude: Double?
    ): CrimeReportResult {
        return try {
            _reportsState.value = _reportsState.value.copy(isLoading = true)
            
            // Upload images
            val imageUrls = images.map { uploadImage(it) }
            
            // Create crime report
            val report = CrimeReport(
                headline = headline,
                description = description,
                location = location,
                crimeType = crimeType,
                suspectDescription = suspectDescription,
                imageUrls = imageUrls,
                reportedBy = userId,
                reporterName = userName,
                latitude = latitude,
                longitude = longitude
            )
            
            // Save to Firestore
            firestore.collection("crime_reports")
                .add(report)
                .await()
            
            loadCrimeReports() // Refresh the list
            CrimeReportResult.Success
        } catch (e: Exception) {
            _reportsState.value = _reportsState.value.copy(isLoading = false)
            CrimeReportResult.Error(e.message ?: "Failed to submit report")
        }
    }

    suspend fun addUpdate(crimeReportId: String, message: String, userId: String, userName: String): CrimeReportResult {
        return try {
            val update = CrimeUpdate(
                crimeReportId = crimeReportId,
                message = message,
                userId = userId,
                userName = userName
            )
            
            firestore.collection("crime_updates")
                .add(update)
                .await()
            
            CrimeReportResult.Success
        } catch (e: Exception) {
            CrimeReportResult.Error(e.message ?: "Failed to add update")
        }
    }

    suspend fun getUpdatesForReport(reportId: String): List<CrimeUpdate> {
        return try {
            val snapshot = firestore.collection("crime_updates")
                .whereEqualTo("crimeReportId", reportId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(CrimeUpdate::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCrimeReport(reportId: String): CrimeReport? {
        return try {
            val doc = firestore.collection("crime_reports")
                .document(reportId)
                .get()
                .await()
            doc.toObject(CrimeReport::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

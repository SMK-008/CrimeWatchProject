package com.example.crimewatch.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crimewatch.data.models.MissingPerson
import com.example.crimewatch.data.models.MissingPersonUpdate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class MissingPersonViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _missingPersons = MutableStateFlow<List<MissingPerson>>(emptyList())
    val missingPersons: StateFlow<List<MissingPerson>> = _missingPersons

    private val _selectedPerson = MutableStateFlow<MissingPerson?>(null)
    val selectedPerson: StateFlow<MissingPerson?> = _selectedPerson

    private val _updates = MutableStateFlow<List<MissingPersonUpdate>>(emptyList())
    val updates: StateFlow<List<MissingPersonUpdate>> = _updates

    init {
        loadMissingPersons()
    }

    private fun loadMissingPersons() {
        viewModelScope.launch {
            try {
                firestore.collection("missing_persons")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        val persons = snapshot?.documents?.mapNotNull {
                            it.toObject(MissingPerson::class.java)
                        } ?: emptyList()
                        _missingPersons.value = persons
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadMissingPerson(personId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("missing_persons").document(personId).get().await()
                _selectedPerson.value = doc.toObject(MissingPerson::class.java)
                loadUpdates(personId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadUpdates(personId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("missing_person_updates")
                    .whereEqualTo("missingPersonId", personId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        val updates = snapshot?.documents?.mapNotNull {
                            it.toObject(MissingPersonUpdate::class.java)
                        } ?: emptyList()
                        _updates.value = updates
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun reportMissingPerson(
        name: String,
        age: Int,
        description: String,
        lastSeenLocation: String,
        lastSeenDate: String,
        contactInfo: String,
        images: List<Uri>,
        latitude: Double?,
        longitude: Double?
    ): String? {
        try {
            val imageUrls = uploadImages(images)
            val currentUser = auth.currentUser ?: return null

            val missingPerson = MissingPerson(
                name = name,
                age = age,
                description = description,
                lastSeenLocation = lastSeenLocation,
                lastSeenDate = lastSeenDate,
                contactInfo = contactInfo,
                imageUrls = imageUrls,
                reportedBy = currentUser.uid,
                reporterName = currentUser.displayName ?: "Anonymous",
                latitude = latitude,
                longitude = longitude
            )

            val docRef = firestore.collection("missing_persons").add(missingPerson).await()
            return docRef.id
        } catch (e: Exception) {
            // Handle error
            return null
        }
    }

    private suspend fun uploadImages(images: List<Uri>): List<String> {
        return images.mapNotNull { uri ->
            try {
                val ref = storage.reference.child("missing_persons/${UUID.randomUUID()}")
                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun addUpdate(
        missingPersonId: String,
        message: String,
        userId: String = auth.currentUser?.uid ?: "",
        userName: String = auth.currentUser?.displayName ?: "Anonymous"
    ) {
        try {
            val update = MissingPersonUpdate(
                missingPersonId = missingPersonId,
                message = message,
                userId = userId,
                userName = userName
            )
            firestore.collection("missing_person_updates").add(update).await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

package com.example.crimewatch.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crimewatch.data.models.CommunityTip
import com.example.crimewatch.data.models.CommunityTipComment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class CommunityTipsState(
    val isLoading: Boolean = false,
    val tips: List<CommunityTip> = emptyList(),
    val error: String? = null
)

class CommunityViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _tipsState = MutableStateFlow(CommunityTipsState())
    val tipsState: StateFlow<CommunityTipsState> = _tipsState

    private val _selectedTip = MutableStateFlow<CommunityTip?>(null)
    val selectedTip: StateFlow<CommunityTip?> = _selectedTip

    private val _comments = MutableStateFlow<List<CommunityTipComment>>(emptyList())
    val comments: StateFlow<List<CommunityTipComment>> = _comments

    init {
        loadCommunityTips()
    }

    private fun loadCommunityTips() {
        viewModelScope.launch {
            try {
                _tipsState.value = _tipsState.value.copy(isLoading = true)
                firestore.collection("community_tips")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            _tipsState.value = _tipsState.value.copy(
                                isLoading = false,
                                error = e.message
                            )
                            return@addSnapshotListener
                        }

                        val tips = snapshot?.documents?.mapNotNull {
                            it.toObject(CommunityTip::class.java)
                        } ?: emptyList()

                        _tipsState.value = _tipsState.value.copy(
                            isLoading = false,
                            tips = tips,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _tipsState.value = _tipsState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadTip(tipId: String) {
        viewModelScope.launch {
            try {
                val doc = firestore.collection("community_tips").document(tipId).get().await()
                _selectedTip.value = doc.toObject(CommunityTip::class.java)
                loadComments(tipId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadComments(tipId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("community_tip_comments")
                    .whereEqualTo("tipId", tipId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) return@addSnapshotListener
                        val comments = snapshot?.documents?.mapNotNull {
                            it.toObject(CommunityTipComment::class.java)
                        } ?: emptyList()
                        _comments.value = comments
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun submitTip(
        title: String,
        description: String,
        category: String,
        location: String,
        images: List<Uri>,
        latitude: Double?,
        longitude: Double?
    ): String? {
        return try {
            val imageUrls = uploadImages(images)
            val currentUser = auth.currentUser ?: return null

            val tip = CommunityTip(
                title = title,
                description = description,
                category = category,
                location = location,
                imageUrls = imageUrls,
                reportedBy = currentUser.uid,
                reporterName = currentUser.displayName ?: "Anonymous",
                latitude = latitude,
                longitude = longitude
            )

            val docRef = firestore.collection("community_tips").add(tip).await()
            docRef.id
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun uploadImages(images: List<Uri>): List<String> {
        return images.mapNotNull { uri ->
            try {
                val ref = storage.reference.child("community_tips/${UUID.randomUUID()}")
                ref.putFile(uri).await()
                ref.downloadUrl.await().toString()
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun addComment(
        tipId: String,
        message: String
    ) {
        try {
            val currentUser = auth.currentUser ?: return
            val comment = CommunityTipComment(
                tipId = tipId,
                message = message,
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "Anonymous"
            )
            firestore.collection("community_tip_comments").add(comment).await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun likeTip(tipId: String) {
        try {
            firestore.collection("community_tips").document(tipId)
                .update("likes", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun incrementViews(tipId: String) {
        try {
            firestore.collection("community_tips").document(tipId)
                .update("views", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

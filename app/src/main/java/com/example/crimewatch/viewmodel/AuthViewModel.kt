package com.example.crimewatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuthException

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null
)

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        // Initialize with current Firebase user
        _authState.value = AuthState(user = auth.currentUser)
    }

    fun login(email: String, password: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    user = auth.currentUser
                )
                onResult(AuthResult.Success)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthException -> {
                        when (e.errorCode) {
                            "ERROR_INVALID_EMAIL" -> "Invalid email address"
                            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
                            else -> e.message ?: "Authentication failed"
                        }
                    }
                    else -> e.message ?: "Authentication failed"
                }
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                onResult(AuthResult.Error(errorMessage))
            }
        }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _authState.value = _authState.value.copy(isLoading = true, error = null)
                
                // Create authentication account
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                
                // Create user profile in Firestore
                authResult.user?.let { user ->
                    val userProfile = hashMapOf(
                        "uid" to user.uid,
                        "email" to email,
                        "name" to name,
                        "createdAt" to System.currentTimeMillis()
                    )
                    
                    firestore.collection("users")
                        .document(user.uid)
                        .set(userProfile)
                        .await()

                    // Send email verification
                    user.sendEmailVerification().await()
                }
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    user = auth.currentUser
                )
                onResult(AuthResult.Success)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthException -> {
                        when (e.errorCode) {
                            "ERROR_WEAK_PASSWORD" -> "Password should be at least 6 characters"
                            "ERROR_INVALID_EMAIL" -> "Invalid email address"
                            "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already registered"
                            else -> e.message ?: "Registration failed"
                        }
                    }
                    else -> e.message ?: "Registration failed"
                }
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
                onResult(AuthResult.Error(errorMessage))
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState()
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null
}

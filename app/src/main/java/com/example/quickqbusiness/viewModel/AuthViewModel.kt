package com.example.quickqbusiness.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickqbusiness.model.OrderData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    private val firestore = FirebaseFirestore.getInstance()


    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        if(auth.currentUser == null){
            _authState.value = AuthState.Unauthenticated
        } else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun signIn(email: String, password: String){

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password cannot be empty.")
        }

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if(task.isSuccessful) {
                    db.collection("owner").get()
                        .addOnSuccessListener { queryDocumentSnapshots->
                            if(!queryDocumentSnapshots.isEmpty) {
                                val users = queryDocumentSnapshots.documents
                                val emailExists = users.any { document ->
                                    document.id == email
                                }
                                if (emailExists) {
                                    _authState.value = AuthState.Authenticated
                                } else {
                                    _authState.value = AuthState.Error(
                                        task.exception?.message ?: "Please log in as a Shopkeeper"
                                    )
                                }

                            } else {
                                _authState.value = AuthState.Error(
                                    task.exception?.message ?: "Something went wrong"
                                )
                            }
                        }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    // LiveData to hold the mail email id
    private val _emailOriginal = MutableLiveData<String>()
    val emailOriginal: LiveData<String> = _emailOriginal

    // Function to fetch the shopId using email as the document ID
    fun fetchOriginalMailByEmail(email: String) {
        firestore.collection("owner").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _emailOriginal.value = document.getString("email") ?: ""
                } else {
                    Log.d("Firestore", "No document found for email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching shopId", e)
            }
    }

    // Function to send password reset email
    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    task.exception?.message?.let { onFailure(it) }
                }
            }
    }
}

sealed class AuthState{
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data class Error(val message: String): AuthState()
}
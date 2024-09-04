package com.example.quickqbusiness

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

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
                                    _authState.value = AuthState.Error(task.exception?.message?:"Please log in as a Shopkeeper")
                                }

                            } else {
                                _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                            }
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState{
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
    data object Loading: AuthState()
    data class Error(val message: String): AuthState()
}
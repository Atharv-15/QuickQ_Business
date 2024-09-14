package com.example.quickqbusiness.data

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quickqbusiness.viewModel.AuthViewModel

@Composable
fun ForgotPasswordDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val ownerMail by authViewModel.emailOriginal.observeAsState()

    // Fetch ownerMail when the composable is launched
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            authViewModel.fetchOriginalMailByEmail(email)
        }
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Forgot Password") },
        text = {
            Column {
                Text(text = "Enter your email to receive a password reset link.")
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = errorMessage ?: "Error sending reset email",
                        color = androidx.compose.ui.graphics.Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                successMessage?.let {
                    Text(
                        text = it,
                        color = androidx.compose.ui.graphics.Color.Green,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (ownerMail?.isNotEmpty() == true) {
                    authViewModel.sendPasswordResetEmail(
                        email = ownerMail!!,
                        onSuccess = {
                            successMessage = "Password reset email sent successfully!"
                            showError = false
                        },
                        onFailure = { error ->
                            errorMessage = error
                            showError = true
                        }
                    )
                } else {
                    showError = true
                    errorMessage = "Please enter a valid email."
                }
            }) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
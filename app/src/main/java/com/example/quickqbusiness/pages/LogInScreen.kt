package com.example.quickqbusiness.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickqbusiness.R
import com.example.quickqbusiness.data.ForgotPasswordDialog
import com.example.quickqbusiness.viewModel.AuthState
import com.example.quickqbusiness.viewModel.AuthViewModel

@Composable
fun LogInScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else ->Unit
        }
    }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.queue),
            contentDescription = "Log In Image",
            modifier = Modifier.size(300.dp)
        )

        Text(text = "Hello", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = "Log In to your account")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = email, onValueChange = {
            email = it
        }, label = {
            Text(text = "Email address")
        }, keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next
        )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = password, onValueChange = {
            password = it
        }, label = {
            Text(text = "Password")
        }, visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            Log.i("Credentials", "Email: $email, Password: $password")
            authViewModel.signIn(email, password)
        }) {
            Text(text = "Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showForgotPasswordDialog = true }) {
            Text(text = "Forgot Password?")
        }

        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                authViewModel = authViewModel,
                onDismiss = { showForgotPasswordDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
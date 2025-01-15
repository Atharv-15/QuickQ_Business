package com.example.quickqbusiness.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.quickqbusiness.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    // Simulate loading time or check login state
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        delay(1000) // Optional delay for smooth transition

        if (currentUser != null) {
            // Navigate to AcceptedOrderPage if user is logged in
            navController.navigate("home") {
                popUpTo("splash_screen") { inclusive = true }
            }
        } else {
            // Navigate to LoginPage if user is not logged in
            navController.navigate("signin") {
                popUpTo("splash_screen") { inclusive = true }
            }
        }
    }

    // UI for displaying the GIF
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = R.drawable.loading, // Place your GIF in `res/drawable`
            contentDescription = "We're cooking!!",
            modifier = Modifier.size(200.dp) // Adjust size as needed
        )
    }
}

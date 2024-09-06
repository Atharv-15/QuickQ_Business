package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signin", builder = {
        composable("signin"){
            LogInScreen(modifier, navController, authViewModel)
        }
        composable("home"){
            MainScreen(modifier, navController, authViewModel)
        }
    })
}
package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickqbusiness.pages.AcceptedOrder
import com.example.quickqbusiness.pages.PendingOrder
import com.example.quickqbusiness.pages.Profile

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
        composable("pending"){
            PendingOrder(modifier, navController, authViewModel)
        }
        composable("profile"){
            Profile(modifier)
        }
        composable("accept"){
            AcceptedOrder(modifier)
        }
    })
}
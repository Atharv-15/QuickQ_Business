package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, shopViewModel: ShopViewModel, orderViewModel: OrderViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signin", builder = {
        composable("signin"){
            LogInScreen(modifier, navController, authViewModel)
        }
        composable("home"){
            MainScreen(modifier, navController, authViewModel, shopViewModel, orderViewModel)
        }
    })
}
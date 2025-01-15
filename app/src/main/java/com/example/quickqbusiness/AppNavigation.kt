package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickqbusiness.pages.AcceptedOrder
import com.example.quickqbusiness.pages.LogInScreen
import com.example.quickqbusiness.pages.SplashScreen
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel,
    orderViewModel: OrderViewModel
) {
    val navController = rememberNavController()

    // Fetch the shopId based on the email
    val shopId by shopViewModel.shopId.observeAsState()

    NavHost(navController = navController, startDestination = "splash_screen", builder = {
        composable("splash_screen"){
            SplashScreen(navController)
        }
        composable("signin"){
            LogInScreen(modifier, navController, authViewModel)
        }
        composable("home"){
            MainScreen(modifier, navController, authViewModel, shopViewModel, orderViewModel)
        }
    })
}
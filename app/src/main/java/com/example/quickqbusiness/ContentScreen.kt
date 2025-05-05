package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quickqbusiness.pages.AcceptedOrder
import com.example.quickqbusiness.pages.PendingOrder
import com.example.quickqbusiness.pages.Profile
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel,
    orderViewModel: OrderViewModel
) {
    // From MainScreen.kt
    val email = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // Fetch the shopId based on the email
    val shopId by shopViewModel.shopId.observeAsState()

    // Fetch shopId when the composable is launched
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            shopViewModel.fetchShopIdByEmail(email)
        }
    }

    NavHost(navController = navController, startDestination = "accepted", modifier = modifier) {
        composable("pending") {
            shopId?.let {
                PendingOrder(modifier, it, navController, authViewModel, orderViewModel)
            }
        }
        composable("accepted") {
            shopId?.let {
                AcceptedOrder(modifier, it, navController, authViewModel, orderViewModel)
            }
        }
        composable("profile") {
            shopId?.let {
                Profile(modifier, it, navController, authViewModel, shopViewModel)
            }
        }
    }
}
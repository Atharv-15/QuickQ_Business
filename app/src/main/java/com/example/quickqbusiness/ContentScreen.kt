package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.quickqbusiness.pages.AcceptedOrder
import com.example.quickqbusiness.pages.PendingOrder
import com.example.quickqbusiness.pages.Profile
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ContentScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, selectedIndex: Int, shopViewModel: ShopViewModel, orderViewModel: OrderViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: ""

    // Fetch the shopId based on the email
    val shopId by shopViewModel.shopId.observeAsState()

    // Fetch shopId when the composable is launched
    LaunchedEffect(email) {
        if (email.isNotEmpty()) {
            shopViewModel.fetchShopIdByEmail(email)
        }
    }

    when(selectedIndex) {
        0 -> {
            // Pass shopId to PendingOrder if it's available
            shopId?.let {
                PendingOrder(modifier, shopId = it, navController, authViewModel, orderViewModel)
            }
        }
        1 -> AcceptedOrder()
        2 -> {
            // Pass shopId to PendingOrder if it's available
            shopId?.let {
                Profile(modifier, authViewModel, shopId = it)
            }
        }
    }
}
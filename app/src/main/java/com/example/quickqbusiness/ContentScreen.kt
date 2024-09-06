package com.example.quickqbusiness

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.quickqbusiness.pages.AcceptedOrder
import com.example.quickqbusiness.pages.PendingOrder
import com.example.quickqbusiness.pages.Profile

@Composable
fun ContentScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel, selectedIndex: Int) {
    when(selectedIndex) {
        0 -> PendingOrder(modifier, navController, authViewModel)
        1 -> AcceptedOrder()
        2 -> Profile(modifier, authViewModel)
    }
}
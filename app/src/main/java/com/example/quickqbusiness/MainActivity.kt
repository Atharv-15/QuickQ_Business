package com.example.quickqbusiness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val shopViewModel: ShopViewModel by viewModels()
        val orderViewModel: OrderViewModel by viewModels()
        setContent {
            AppNavigation(modifier = Modifier, authViewModel = authViewModel, shopViewModel = shopViewModel, orderViewModel = orderViewModel)
        }
    }
}
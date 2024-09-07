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


//package com.example.quickqbusiness.data
//
//import com.example.quickqbusiness.model.OrderData
//import com.example.quickqbusiness.model.OrderItemData
//// used to load sample orders
//class Order {
//    fun loadOrders(): List<OrderData> {
//        return listOf(
////            OrderData(
////                description = "Order #123",
////                totalPrice = "500",
////                items = listOf(
////                    OrderItemData("Item 1", "2", "100"),
////                    OrderItemData("Item 2", "1", "200"),
////                    OrderItemData("Item 3", "3", "300")
////                )
////            ),
////            OrderData(
////                description = "Order #124",
////                totalPrice = "300",
////                items = listOf(
////                    OrderItemData("Item 1", "1", "150"),
////                    OrderItemData("Item 2", "2", "150")
////                )
////            ),
////            OrderData(
////                description = "Order #125",
////                totalPrice = "700",
////                items = listOf(
////                    OrderItemData("Item 1", "4", "400"),
////                    OrderItemData("Item 2", "3", "300")
////                )
////            )
//        )
//    }
//}
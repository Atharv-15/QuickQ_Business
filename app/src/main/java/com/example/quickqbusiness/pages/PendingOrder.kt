package com.example.quickqbusiness.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickqbusiness.viewModel.AuthState
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.R
import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.model.OrderDataWithId
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PendingOrder(modifier: Modifier, shopId: String, navController: NavController, authViewModel: AuthViewModel, orderViewModel: OrderViewModel) {

    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("signin")
            else -> Unit
        }
    }

    val layoutDirection = LocalLayoutDirection.current

    // Observe the orderListWithIds from the ViewModel
    val orderList by orderViewModel.orderListWithIds.observeAsState(emptyList())

    // Start listening for pending orders
    LaunchedEffect(shopId) {
        orderViewModel.startListeningForPendingOrders(shopId)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateEndPadding(layoutDirection)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Pending Orders", fontSize = 32.sp)
            LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
                items(orderList) { orderDataWithId ->
                    OrderCard(
                        order = orderDataWithId.orderData,
                        orderId = orderDataWithId.id,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun OrderCard(order: OrderData, orderId: String, modifier: Modifier = Modifier) {

    var customerName by remember { mutableStateOf("") } // Store customer name
    var totalAmount by remember { mutableIntStateOf(order.totalPrice) } // Store total amount

    // Fetch userId from Firestore when this composable loads

    // Fetch totalPrice from Firestore when this composable loads
    LaunchedEffect(orderId) {
        val orderRef = FirebaseFirestore.getInstance().collection("orders").document(orderId)
        orderRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val fetchedTotalPrice = document.getLong("totalAmount")
                totalAmount = fetchedTotalPrice?.toInt() ?: order.totalPrice

                val fetchedUserName = document.getString("userName")
                customerName = fetchedUserName ?: "Unknown"
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // First row: Name, Total Price, Remove, Accept buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = customerName, // Order Name
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f) // Take available space
                )

                Text(
                    text = "Total: $totalAmount", // Total Price
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Image(
                    painter = painterResource(R.drawable.remove), // Default remove icon
                    contentDescription = "Remove Order",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .height(24.dp)
                        .clickable {
                            // Handle remove order
                        }
                )

                Image(
                    painter = painterResource(R.drawable.check), // Default accept icon
                    contentDescription = "Accept Order",
                    modifier = Modifier
                        .height(24.dp)
                        .clickable {
                            // Handle accept order
                        }
                )
            }

            // Second row: List of items (not scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name, // Item name
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Qty: ${item.quantity}", // Item quantity
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = "Price: ${item.price}", // Item price
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Image(
                            painter = painterResource(R.drawable.remove), // Default remove item icon
                            contentDescription = "Remove Item",
                            modifier = Modifier
                                .height(24.dp)
                                .clickable {
                                    // Handle remove item
                                }
                        )
                    }
                }
            }
        }
    }
}

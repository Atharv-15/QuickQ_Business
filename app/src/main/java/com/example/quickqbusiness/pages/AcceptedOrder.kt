package com.example.quickqbusiness.pages

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickqbusiness.data.AcceptedOrderCard
import com.example.quickqbusiness.data.ExitConfirmationDialog
import com.example.quickqbusiness.viewModel.AuthState
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel

@Composable
fun AcceptedOrder(
    modifier: Modifier,
    shopId: String,
    navController: NavController,
    authViewModel: AuthViewModel,
    orderViewModel: OrderViewModel
) {

    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("signin")
            else -> Unit
        }
    }

//    val layoutDirection = LocalLayoutDirection.current

    val showDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current // Get the context

    // Handle back press
    BackHandler {
        showDialog.value = true // Show confirmation dialog
    }

    // Show exit confirmation dialog if needed
    if (showDialog.value) {
        ExitConfirmationDialog(
            onConfirm = {
                showDialog.value = false
                (context as? Activity)?.finish()
            },
            onDismiss = { showDialog.value = false }
        )
    }

    // Observe the orderListWithIds from the ViewModel
    val orderList by orderViewModel.acceptedOrderListWithIds.observeAsState(emptyList())

    // Start listening for pending orders
    LaunchedEffect(shopId) {
        orderViewModel.startListeningForAcceptedOrders(shopId)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Accepted Orders",
                fontSize = 32.sp
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orderList) { orderDataWithId ->
                    AcceptedOrderCard(
                        order = orderDataWithId.orderData,
                        orderId = orderDataWithId.id,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
package com.example.quickqbusiness.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickqbusiness.data.AcceptedOrderCard
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

    val layoutDirection = LocalLayoutDirection.current

    // Observe the orderListWithIds from the ViewModel
    val orderList by orderViewModel.acceptedOrderListWithIds.observeAsState(emptyList())

    // Start listening for pending orders
    LaunchedEffect(shopId) {
        orderViewModel.startListeningForAcceptedOrders(shopId)
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
            modifier = Modifier.padding(8.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Accepted Orders",
                fontSize = 32.sp
            )
            LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)) {
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
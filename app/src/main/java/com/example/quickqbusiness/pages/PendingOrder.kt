package com.example.quickqbusiness.pages

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quickqbusiness.AuthState
import com.example.quickqbusiness.AuthViewModel
import com.example.quickqbusiness.R
import com.example.quickqbusiness.data.Order
import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.ui.theme.QuickQBusinessTheme


@Composable
fun PendingOrder(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    val layoutDirection = LocalLayoutDirection.current

    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("signin")
            else -> Unit
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
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
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Pending Orders", fontSize = 32.sp)
            OrderList(orderList = Order().loadOrders())
            TextButton(onClick = {authViewModel.signOut()}) {
                Text(text = "Sign Out")
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderData, modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = order.description,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.headlineSmall
            )
            Row (
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.price,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = order.quantity,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.headlineSmall
                )
                Image(
                    painter = painterResource(order.remove),
                    contentDescription = "Remove Item",
                    modifier = Modifier
                        .padding(6.dp)
                        .height(30.dp),
                    //                contentScale = ContentScale.Crop
                )
                Image(
                    painter = painterResource(order.check),
                    contentDescription = "Confirm Item",
                    modifier = Modifier
                        .padding(8.dp)
                        .height(30.dp),
                    //                contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun OrderList(orderList: List<OrderData>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = Modifier) {
        items(orderList) { order ->
            OrderCard(
                order = order,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickQBusinessTheme {
        OrderCard(OrderData("Item 1", "3", "300", R.drawable.remove, R.drawable.check))
    }
}
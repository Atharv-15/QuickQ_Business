package com.example.quickqbusiness.data

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quickqbusiness.R
import com.example.quickqbusiness.model.OrderData
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderCard(order: OrderData, orderId: String, modifier: Modifier = Modifier) {
    // Used in PendingOrder.kt
    var customerName by remember { mutableStateOf("") } // Store customer name
    var totalAmount by remember { mutableIntStateOf(order.totalPrice) } // Store total amount

    // Fetch totalPrice and customerName from Firestore when this composable loads
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

    var showDialog by remember { mutableStateOf(false) } // Control dialog visibility

    // Confirm Order Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Order") },
            text = { Text("Are you sure you want to accept this order?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    acceptOrder(orderId)
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
                    fontWeight = FontWeight.Bold,
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
                            showDialog = true
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

fun acceptOrder(orderId: String) {
    val orderRef = FirebaseFirestore.getInstance().collection("orders").document(orderId)

    // Update the status field to "Confirmed"
    orderRef.update("status", "Confirmed")
        .addOnSuccessListener {
            Log.d("OrderStatus", "Order $orderId confirmed successfully.")
        }
        .addOnFailureListener { e ->
            Log.w("OrderStatus", "Error confirming order $orderId", e)
        }
}
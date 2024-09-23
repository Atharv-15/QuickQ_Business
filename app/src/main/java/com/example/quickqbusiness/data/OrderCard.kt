package com.example.quickqbusiness.data

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.quickqbusiness.R
import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.model.acceptOrder
import com.example.quickqbusiness.model.declineOrder
import com.example.quickqbusiness.model.formatTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun OrderCard(order: OrderData, orderId: String, modifier: Modifier = Modifier) {
    // Used in PendingOrder.kt
    var customerName by remember { mutableStateOf("") } // Store customer name
    var totalAmount by remember { mutableIntStateOf(order.totalPrice) } // Store total amount
    var note by remember { mutableStateOf("") } // Store order notes from customer
    var time by remember { mutableStateOf(formatTimestamp(Timestamp.now())) }
    var isAnyItemSelected by remember { mutableStateOf(false) } // Check if at least one item is not removed
    var isAnyItemRemoved by remember { mutableStateOf(false) } // Check if at least one item is not removed
    val itemState = remember { mutableStateListOf(*order.items.toTypedArray()) } // Track item status locally

    val context = LocalContext.current // Get the current context

    // Fetch totalPrice and customerName from Firestore when this composable loads
    LaunchedEffect(orderId) {
        val orderRef = FirebaseFirestore.getInstance().collection("orders").document(orderId)
        orderRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                totalAmount = document.getLong("totalAmount")?.toInt() ?: order.totalPrice
                customerName = document.getString("userName") ?: "Unknown"
                note = document.getString("note") ?: ""
                if (note == "") note = "Have a good day :)"
                // Get the time from the Firestore document and format it
                val firestoreTimestamp = document.getTimestamp("timestamp") ?: order.time
                time = formatTimestamp(firestoreTimestamp) // Set the formatted time
            }
        }
    }

    // Check if there's any item left that is not removed
    LaunchedEffect(itemState) {
        isAnyItemSelected = itemState.any { it.itemStatus == "Selected" }
    }

    var showDialog by remember { mutableStateOf(false) } // Control dialog visibility
    var selectedTime by remember { mutableIntStateOf(-1) } // Track selected estimate time

    // Confirm Order Dialog
    if (showDialog) {
        var titleText = "Confirm Order"
        isAnyItemRemoved = itemState.any { it.itemStatus == "Removed" }
        if (isAnyItemRemoved) titleText = "Confirm Partial Order"

//        var selectedTime by remember { mutableStateOf(-1) } // No time selected initially

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(titleText) },
            text = {
                Column {
                    Text("Are you sure you want to accept this order?")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Estimate time buttons
                    Column {
                        listOf(5, 10, 15, 20, 30, 45, 60).forEach { time ->
                            Button(
                                onClick = { selectedTime = time },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedTime == time) Color.Gray else Color.LightGray
                                )
                            ) {
                                Text("$time min")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (selectedTime == -1) {
                        // Show Toast if the reason is empty
                        Toast.makeText(context, "Please enter an estimated time :)", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        showDialog = false
                        acceptOrder(orderId, totalAmount, itemState, selectedTime) // Pass selectedTime
                        selectedTime = -1
                    }
                },
                    modifier = Modifier.alpha(if (selectedTime == -1) 0.5f else 1f) // Change opacity when disabled
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    selectedTime = -1
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Decline Order Dialog
    var showDeclineDialog by remember { mutableStateOf(false) } // Control decline dialog visibility
    var declineReason by remember { mutableStateOf("") } // Store selected decline reason

    // Decline Order Dialog
    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline Order") },
            text = {
                Column {
                    // Input for manual reason
                    OutlinedTextField(
                        value = declineReason,
                        onValueChange = { declineReason = it },
                        label = { Text("Please enter reason for decline...") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Pre-defined reasons
                    listOf(
                        "Items not available",
                        "Order queue too long",
                        "Currently not accepting orders",
                        "About to close"
                    ).forEach { reason ->
                        Button(
                            onClick = { declineReason = reason },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(text = reason)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (declineReason.isBlank()) {
                            // Show Toast if the reason is empty
                            Toast.makeText(context, "Please enter the reason for decline", Toast.LENGTH_SHORT).show()
                        } else {
                            showDeclineDialog = false
                            declineOrder(orderId, declineReason) // Trigger decline order callback
                            declineReason = ""
                        }
                    },
                    modifier = Modifier.alpha(if (declineReason.isBlank()) 0.5f else 1f) // Change opacity when disabled
                ) {
                    Text("Decline")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDeclineDialog = false
                    declineReason = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Accept order logic
    fun handleAcceptOrder() {
        if (isAnyItemSelected) {
            // Update order status and items status in Firebase
            showDialog = true
        } else {
            // Show a toast saying "Please select at least one item."
            Toast.makeText(context, "Please select at least one item", Toast.LENGTH_SHORT).show()
        }
    }

    // Toggle the status of an item (Selected/Removed)
    fun toggleItemStatus(itemId: String) {
        val index = itemState.indexOfFirst { it.id == itemId }
        if (index != -1) {
            val currentItem = itemState[index]
            val newStatus = if (currentItem.itemStatus == "Selected") "Removed" else "Selected"
            itemState[index] = currentItem.copy(itemStatus = newStatus)

            // Update the total price
            if (newStatus == "Removed") totalAmount -= (currentItem.price * currentItem.quantity) else totalAmount += (currentItem.price * currentItem.quantity)

            // Recheck if there is any item selected after toggling
            isAnyItemSelected = itemState.any { it.itemStatus == "Selected" }
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
                    text = "Total: ₹ $totalAmount", // Total Price
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
                            showDeclineDialog = true
                        }
                )

                Image(
                    painter = painterResource(if (isAnyItemSelected) R.drawable.check else R.drawable.checkalt), // Toggle icon
                    contentDescription = "Accept Order",
                    modifier = Modifier
                        .height(24.dp)
                        .clickable {
                            // Handle accept order
                            handleAcceptOrder()
                        }
                )
            }

            // Second row: List of items (not scrollable)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                itemState.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .alpha(if (item.itemStatus == "Removed") 0.5f else 1f), // Dim the row if item is removed
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
                                    toggleItemStatus(item.id) // Toggle item status
                                }
                        )
                    }
                }
            }
            Text(
                text = "Note: $note", // Total Price
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Order Time: $time",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
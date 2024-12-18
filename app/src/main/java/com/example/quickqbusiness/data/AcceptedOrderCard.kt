package com.example.quickqbusiness.data

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.quickqbusiness.model.AcceptedOrderData
import com.example.quickqbusiness.model.formatTimestamp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AcceptedOrderCard(order: AcceptedOrderData, orderId: String, modifier: Modifier = Modifier) {
    var customerName by remember { mutableStateOf("") }
    var totalAmount by remember { mutableIntStateOf(order.totalPrice) }
    var isOTPVisible by remember { mutableStateOf(false) } // Track OTP visibility
    var otpInput by remember { mutableStateOf(TextFieldValue("")) } // Store OTP input
    var errorMessage by remember { mutableStateOf("") } // Store error message
    var note by remember { mutableStateOf("") } // Store order notes from customer
    var time by remember { mutableStateOf(formatTimestamp(Timestamp.now())) }

    // Fetch totalPrice and customerName from Firestore
    LaunchedEffect(orderId) {
        val orderRef = FirebaseFirestore.getInstance().collection("paidOrders").document(orderId)
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

    val context = LocalContext.current // Get the current context

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // First row: Name, Total Price, Deliver button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = customerName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Total: $totalAmount",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Button(
                    onClick = {
                        isOTPVisible = !isOTPVisible
                        errorMessage = ""
                        otpInput = TextFieldValue("")
                    } // Show OTP input when clicked
                ) {
                    Text(if (isOTPVisible) "Hide" else "Deliver")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Second row: List of items
            Column(modifier = Modifier.fillMaxWidth()) {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, modifier = Modifier.weight(1f))
                        Text(text = "Qty: ${item.quantity}")
                    }
                }
            }

            // OTP Entry Section
            if (isOTPVisible) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("Enter OTP") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            validateOTP(orderId, otpInput.text, onSuccess = {
                                Toast.makeText(context, "Order successfully delivered :)", Toast.LENGTH_SHORT).show()
                                isOTPVisible = !isOTPVisible
                                errorMessage = ""
                            }, onFailure = {
                                errorMessage = "Invalid OTP"
                            })
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Note: $note", // Total Price
                style = MaterialTheme.typography.bodyMedium,
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

fun validateOTP(orderId: String, enteredOTP: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val orderRef = db.collection("paidOrders").document(orderId)

    orderRef.get().addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            val storedOTP = document.getString("otp") // Assuming "otp" field in Firebase
            if (storedOTP == enteredOTP) {
                // OTP matches, proceed to copy the document to 'historyOrders'
                val orderData = document.data // Get the entire document data

                // Add the document to the 'historyOrders' collection
                db.collection("historyOrders")
                    .document(orderId)
                    .set(orderData!!)
                    .addOnSuccessListener {
                        // After successfully copying, delete the document from 'paidOrders'
                        db.collection("paidOrders").document(orderId)
                            .delete()
                            .addOnSuccessListener {
                                // Call onSuccess after moving the document
                                onSuccess()
                            }
                            .addOnFailureListener {
                                // Handle failure of deletion
                                onFailure()
                            }
                    }
                    .addOnFailureListener {
                        // Handle failure of copying to 'historyOrders'
                        onFailure()
                    }
            } else {
                // OTP does not match
                onFailure()
            }
        } else {
            // Document does not exist
            onFailure()
        }
    }.addOnFailureListener {
        // Handle failure to retrieve the document
        onFailure()
    }
}

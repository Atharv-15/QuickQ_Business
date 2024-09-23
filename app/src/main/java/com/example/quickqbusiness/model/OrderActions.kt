package com.example.quickqbusiness.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

fun acceptOrder(orderId: String, total: Int, items: List<OrderItemData>, estimateTime: Int) {
    val orderRef = FirebaseFirestore.getInstance().collection("orders").document(orderId)

    // Update the order's status and the status of each item
    val updatedItems = items.map { item ->
        mapOf(
            "id" to item.id,
            "name" to item.name,
            "quantity" to item.quantity,
            "price" to item.price,
            "itemStatus" to item.itemStatus
        )
    }

    orderRef.update(
        "items", updatedItems,
        "status", "Confirmed",
        "totalAmount", total,
        "estimateTime", estimateTime
    )
        .addOnSuccessListener {
            Log.d("OrderStatus", "Order $orderId accepted with items updated.")
        }
        .addOnFailureListener { e ->
            Log.w("OrderStatus", "Error accepting order $orderId", e)
        }
}

fun declineOrder(orderId: String, reason: String) {
    val orderRef = FirebaseFirestore.getInstance().collection("orders").document(orderId)

    // Update the status and add a new field called declineReason
    orderRef.update(
        mapOf(
            "status" to "Declined",
            "declineReason" to reason // Add a new field for the reason
        )
    )
        .addOnSuccessListener {
            Log.d("OrderStatus", "Order $orderId declined with reason: $reason")
        }
        .addOnFailureListener { e ->
            Log.w("OrderStatus", "Error declining order $orderId", e)
        }
}

// Helper function to format the timestamp
fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val date = timestamp.toDate() // Convert Firestore Timestamp to Date
    return sdf.format(date)
}
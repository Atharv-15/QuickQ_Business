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
    val db = FirebaseFirestore.getInstance()
    val orderRef = db.collection("orders").document(orderId)
    val declinedRef = db.collection("declinedOrders").document(orderId)

    orderRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val orderData = document.data?.toMutableMap() ?: mutableMapOf()
                orderData["status"] = "Declined"
                orderData["declineReason"] = reason

                // Add to declinedOrders
                declinedRef.set(orderData)
                    .addOnSuccessListener {
                        // Delete original order after successful move
                        orderRef.delete()
                            .addOnSuccessListener {
                                Log.d("OrderStatus", "Order $orderId declined and moved to declinedOrders.")
                            }
                            .addOnFailureListener { e ->
                                Log.w("OrderStatus", "Failed to delete original order $orderId", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("OrderStatus", "Failed to add order to declinedOrders", e)
                    }
            } else {
                Log.w("OrderStatus", "Order $orderId not found.")
            }
        }
        .addOnFailureListener { e ->
            Log.w("OrderStatus", "Failed to retrieve order $orderId", e)
        }
}

// Helper function to format the timestamp
fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val date = timestamp.toDate() // Convert Firestore Timestamp to Date
    return sdf.format(date)
}
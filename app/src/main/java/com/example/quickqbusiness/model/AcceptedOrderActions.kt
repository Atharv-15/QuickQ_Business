package com.example.quickqbusiness.model

import com.google.firebase.firestore.FirebaseFirestore

fun validateOTP(orderId: String, enteredOTP: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val orderRef = db.collection("paidOrders").document(orderId)

    orderRef.get().addOnSuccessListener { document ->
        if (document != null && document.exists()) {
            val storedOTP = document.getString("otp") // Assuming "otp" field in Firebase
            if (storedOTP == enteredOTP) {
                // OTP matches, proceed to copy the document to 'historyOrders'
                val orderData = document.data?.toMutableMap() ?: mutableMapOf() // Get the entire document data
                orderData["status"] = "Completed"

                // Add the document to the 'historyOrders' collection
                db.collection("historyOrders")
                    .document(orderId)
                    .set(orderData)
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
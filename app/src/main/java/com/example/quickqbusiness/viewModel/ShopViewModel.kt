package com.example.quickqbusiness.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ShopViewModel : ViewModel() {
    // Used in ContentScreen.kt
    private val firestore = FirebaseFirestore.getInstance()

    // LiveData to hold the shopId
    private val _shopId = MutableLiveData<String>()
    val shopId: LiveData<String> = _shopId

    // Function to fetch the shopId using email as the document ID
    fun fetchShopIdByEmail(email: String) {
        firestore.collection("owner").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _shopId.value = document.getString("shopId") ?: ""
                } else {
                    Log.d("Firestore", "No document found for email: $email")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching shopId", e)
            }
    }

    // LiveData for shop details
    private val _shopDetails = MutableLiveData<ShopDetails>()
    val shopDetails: LiveData<ShopDetails> = _shopDetails

    // Function to fetch shop details using shop id as document ID
    fun fetchShopDetailsById(shopId: String) {
        firestore.collection("shops").document(shopId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Map Firestore document fields to ShopDetails data class
                    val shopDetails = ShopDetails(
                        shopName = document.getString("name") ?: "",
                        gstNumber = document.getString("gstNumber") ?: "",
                        aadhaarNumber = document.getString("aadhaarNumber") ?: "",
                        ownerName = document.getString("ownerName") ?: "",
                        phoneNumber = document.get("Contact")?.toString() ?: "N/A",
                        address = document.getString("address") ?: ""
                    )
                    _shopDetails.value = shopDetails
                } else {
                    Log.d("Firestore", "No document found for email: $shopId")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching shop details", e)
            }
    }
}


// Data class to hold the shop details
data class ShopDetails(
    val shopName: String = "",
    val gstNumber: String = "",
    val aadhaarNumber: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)
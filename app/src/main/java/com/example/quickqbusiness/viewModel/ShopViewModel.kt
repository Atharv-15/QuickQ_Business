package com.example.quickqbusiness.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ShopViewModel : ViewModel() {

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
}
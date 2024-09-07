package com.example.quickqbusiness.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.model.OrderDataWithId
import com.google.firebase.firestore.FirebaseFirestore

class OrderViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _orderListWithIds = MutableLiveData<List<OrderDataWithId>>()
    val orderListWithIds: LiveData<List<OrderDataWithId>> = _orderListWithIds


    // Function to start listening to pending orders
    fun startListeningForPendingOrders(shopId: String) {
        firestore.collection("orders")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orderListWithIds = mutableListOf<OrderDataWithId>()

                    for (document in snapshot.documents) {
                        // Extract the shopId from the document ID by splitting the string
                        val documentShopId = document.id.split("_")[0]

                        // Only process documents where the shopId matches
                        if (documentShopId == shopId) {
                            val orderData = document.toObject(OrderData::class.java)
                            orderData?.let {
                                orderListWithIds.add(OrderDataWithId(document.id, it))
                            }
                        }
                    }

                    _orderListWithIds.value = orderListWithIds // Update LiveData
                }
            }
    }
}
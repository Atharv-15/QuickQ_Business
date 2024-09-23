package com.example.quickqbusiness.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.quickqbusiness.model.AcceptedOrderData
import com.example.quickqbusiness.model.AcceptedOrderDataWithId
import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.model.OrderDataWithId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class OrderViewModel: ViewModel() {
    // Used in PendingOrder.kt and AcceptedOrder.kt
    private val firestore = FirebaseFirestore.getInstance()

    // For Pending Orders
    private val _orderListWithIds = MutableLiveData<List<OrderDataWithId>>()
    val orderListWithIds: LiveData<List<OrderDataWithId>> = _orderListWithIds

    // Function to start listening to pending orders
    fun startListeningForPendingOrders(shopId: String) {
        firestore.collection("orders")
            .whereEqualTo("status", "Pending")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Ordering by the "time" field in ascending order
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

    // For Accepted Orders
    private val _acceptedOrderListWithIds = MutableLiveData<List<AcceptedOrderDataWithId>>()
    val acceptedOrderListWithIds: LiveData<List<AcceptedOrderDataWithId>> = _acceptedOrderListWithIds

    // Function to start listening to accepted orders
    fun startListeningForAcceptedOrders(shopId: String) {
        firestore.collection("paidOrders")
            .whereEqualTo("status", "Paid")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val acceptedOrderListWithIds = mutableListOf<AcceptedOrderDataWithId>()

                    for (document in snapshot.documents) {
                        // Extract the shopId from the document ID by splitting the string
                        val documentShopId = document.id.split("_")[0]

                        // Only process documents where the shopId matches
                        if (documentShopId == shopId) {
                            val orderData = document.toObject(AcceptedOrderData::class.java)
                            orderData?.let {
                                acceptedOrderListWithIds.add(AcceptedOrderDataWithId(document.id, it))
                            }
                        }
                    }

                    _acceptedOrderListWithIds.value = acceptedOrderListWithIds // Update LiveData
                }
            }
    }
}
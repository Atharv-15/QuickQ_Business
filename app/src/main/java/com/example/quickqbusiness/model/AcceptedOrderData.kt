package com.example.quickqbusiness.model

import com.google.firebase.Timestamp

data class AcceptedOrderData(
    val totalPrice: Int,
    val time: Timestamp,
    val items: List<AcceptedOrderItemData> // List of items in the order
){
    // No-argument constructor
    constructor() : this(0, Timestamp.now(), emptyList())
}
data class AcceptedOrderItemData(
    val name: String,
    val quantity: Int,
    val price: Int
){
    // No-argument constructor
    constructor() : this("", 0, 0)
}
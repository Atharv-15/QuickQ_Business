package com.example.quickqbusiness.model

import com.google.firebase.Timestamp

data class OrderData(
    val totalPrice: Int,
    val notes: String,
    val time: Timestamp,
    val items: List<OrderItemData> // List of items in the order
){
    // No-argument constructor
    constructor() : this(0, "", Timestamp.now(), emptyList())
}
data class OrderItemData(
    val name: String,
    val itemStatus: String,
    val id: String,
    val quantity: Int,
    val price: Int
){
    // No-argument constructor
    constructor() : this("", "", "", 0, 0)
}
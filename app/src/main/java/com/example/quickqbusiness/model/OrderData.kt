package com.example.quickqbusiness.model

data class OrderData(
    val description: String,
    val totalPrice: String,
    val items: List<OrderItemData> // List of items in the order
)

data class OrderItemData(
    val name: String,
    val quantity: String,
    val price: String
)

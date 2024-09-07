package com.example.quickqbusiness.model

data class OrderData(
    val totalPrice: Int,
    val items: List<OrderItemData> // List of items in the order
){
    // No-argument constructor
    constructor() : this(0, emptyList())
}
data class OrderItemData(
    val name: String,
    val quantity: Int,
    val price: Int
){
    // No-argument constructor
    constructor() : this("", 0, 0)
}
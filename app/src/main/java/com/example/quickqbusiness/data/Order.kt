package com.example.quickqbusiness.data

import com.example.quickqbusiness.model.OrderData
import com.example.quickqbusiness.model.OrderItemData

class Order {
    fun loadOrders(): List<OrderData> {
        return listOf(
            OrderData(
                description = "Order #123",
                totalPrice = "500",
                items = listOf(
                    OrderItemData("Item 1", "2", "100"),
                    OrderItemData("Item 2", "1", "200"),
                    OrderItemData("Item 3", "3", "300")
                )
            ),
            OrderData(
                description = "Order #124",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            ),
            OrderData(
                description = "Order #125",
                totalPrice = "700",
                items = listOf(
                    OrderItemData("Item 1", "4", "400"),
                    OrderItemData("Item 2", "3", "300")
                )
            ),
            OrderData(
                description = "Order #126",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            ),
            OrderData(
                description = "Order #127",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            ),
            OrderData(
                description = "Order #128",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            ),
            OrderData(
                description = "Order #129",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            ),
            OrderData(
                description = "Order #130",
                totalPrice = "300",
                items = listOf(
                    OrderItemData("Item 1", "1", "150"),
                    OrderItemData("Item 2", "2", "150")
                )
            )
        )
    }
}
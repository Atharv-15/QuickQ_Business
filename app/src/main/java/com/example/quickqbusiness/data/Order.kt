package com.example.quickqbusiness.data

import androidx.annotation.DrawableRes
import com.example.quickqbusiness.R
import com.example.quickqbusiness.model.OrderData

class Order() {
    fun loadOrders(): List<OrderData> {
        return listOf<OrderData>(
            OrderData(
                description = "Paneer Tikka",
                quantity = "1",
                price = "100",
                remove = R.drawable.remove,
                check = R.drawable.check
            ),
            OrderData(
                description = "Chole Samose",
                quantity = "1",
                price = "50",
                remove = R.drawable.remove,
                check = R.drawable.check
            ),
            OrderData(
                description = "Omlette",
                quantity = "2",
                price = "40",
                remove = R.drawable.remove,
                check = R.drawable.check
            )
        )
    }
}
package com.example.quickqbusiness.model

import androidx.annotation.DrawableRes

data class OrderData(
    val description: String,
    val quantity: String,
    val price: String,
    @DrawableRes val remove: Int,
    @DrawableRes val check: Int
)

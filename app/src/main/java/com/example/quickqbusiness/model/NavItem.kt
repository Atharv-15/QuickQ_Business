package com.example.quickqbusiness.model

import androidx.annotation.DrawableRes

data class NavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
    val count: Int
)

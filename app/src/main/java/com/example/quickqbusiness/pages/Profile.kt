package com.example.quickqbusiness.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel

@Composable
fun Profile(
    modifier: Modifier = Modifier,
    shopId: String,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel
) {
    val layoutDirection = LocalLayoutDirection.current
    val scrollState = rememberScrollState() // Remember scroll state for scrolling

    val shopDetails by shopViewModel.shopDetails.observeAsState()

    // Fetch shop details when shopId is available
    LaunchedEffect(shopId) {
        if (shopId.isNotEmpty()) {
            shopViewModel.fetchShopDetailsById(shopId)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                start = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateStartPadding(layoutDirection),
                end = WindowInsets.safeDrawing
                    .asPaddingValues()
                    .calculateEndPadding(layoutDirection)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween, // Space between items
            horizontalAlignment = Alignment.Start
        ) {
            // Title text
            Text(
                text = "Profile",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Shop details display
                shopDetails?.let { shop ->
                    ProfileDetail(label = "Shop Name", value = shop.shopName)
                    ProfileDetail(label = "GST Number", value = shop.gstNumber)
                    ProfileDetail(label = "Aadhaar Number", value = shop.aadhaarNumber)
                    ProfileDetail(label = "Owner Name", value = shop.ownerName)
                    ProfileDetail(label = "Phone Number", value = shop.phoneNumber)
                    ProfileDetail(label = "Address", value = shop.address)
                    ProfileDetail(label = "Shop Id", value = shopId)
                }

                // Sign out button
                TextButton(
                    onClick = { authViewModel.signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .padding(bottom = 64.dp)// Padding around the button
                ) {
                    Text(text = "Sign Out", fontSize = 18.sp)
                }
            }
        }
    }
}


@Composable
fun ProfileDetail(label: String, value: String?) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth() // Ensure it takes full width
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary, // Use a primary color for the label
            fontWeight = FontWeight.Bold, // Make label bold for emphasis
            modifier = Modifier.padding(bottom = 4.dp) // Add space below label
        )
        Text(
            text = value ?: "N/A",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal, // Lighter font for value
            color = MaterialTheme.colorScheme.onSurface, // Use a standard text color
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(4.dp)) // Add background
                .padding(12.dp) // Inner padding
        )
    }
}

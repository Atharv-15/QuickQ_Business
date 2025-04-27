package com.example.quickqbusiness.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quickqbusiness.R
import com.example.quickqbusiness.ui.theme.DarkYellow
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Profile(
    modifier: Modifier = Modifier,
    shopId: String,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel
) {
    val shopDetails by shopViewModel.shopDetails.observeAsState()

    LaunchedEffect(shopId) {
        if (shopId.isNotEmpty()) {
            shopViewModel.fetchShopDetailsById(shopId)
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = { authViewModel.signOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 16.dp, bottom = 145.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFd83535)
                )
            ) {
                Text(text = "Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        color = colorResource(id = R.color.primary_green),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Profile Picture
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.shop),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Owner Name and Shop Name
                shopDetails?.let { shop ->
                    Text(
                        text = shop.ownerName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = shop.shopName,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // White scrollable surface
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color(0xFFfff3f3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Buttons (fixed within the white surface)
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            maxItemsInEachRow = 3
                        ) {
                            ProfileOptionButton(
                                label = "History",
                                iconResId = R.drawable.history,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileOptionButton(
                                label = "Certificates",
                                iconResId = R.drawable.certificate,
                                modifier = Modifier.weight(1f)
                            )
                            ProfileOptionButton(
                                label = "Analytics",
                                iconResId = R.drawable.analytics,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Scrollable details only
                        Column(
                            modifier = Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))

                            shopDetails?.let { shop ->
                                ProfileDetailItem("Phone Number", shop.phoneNumber)
                                Spacer(modifier = Modifier.height(12.dp))
//                              ProfileDetailItem("Email ID", shop.email)
                                ProfileDetailItem("Address", shop.address)
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileDetailItem("Shop ID", shopId)
                                Spacer(modifier = Modifier.height(12.dp))
                                ProfileDetailItem("GST Number", shop.gstNumber)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ProfileOptionButton(
    label: String,
    iconResId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkYellow),
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp),
                color = Color.Black
            )
        }
    }
}



@Composable
fun ProfileDetailItem(label: String, value: String?) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colorResource(id = R.color.primary_green)
            )
            Text(
                text = value ?: "N/A",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
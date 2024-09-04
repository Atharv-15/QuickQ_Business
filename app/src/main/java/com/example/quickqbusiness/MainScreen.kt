package com.example.quickqbusiness

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.quickqbusiness.model.NavItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.quickqbusiness.data.Order

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("signin")
            else -> Unit
        }
    }

    val pendingSize by remember {
        derivedStateOf {
            Order().loadOrders().size
        }
    }

    val navItemList = listOf(
        NavItem("Pending", R.drawable.pending, pendingSize),
        NavItem("Accepted", R.drawable.accept, 0),
        NavItem("Profile", R.drawable.profile, 0)
    )
    var selectedIndex by remember {
        mutableIntStateOf(1)
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItemList.forEachIndexed { index, navItem ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (navItem.count > 0) {
                                    Badge {
                                        Text(text = navItem.count.toString())
                                    }
                                }
                            }) {
                                Icon(painter = painterResource(id = navItem.icon), contentDescription = "Icon")
                            }
                        },
                        label = {
                            Text(text = navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(modifier = Modifier.padding(innerPadding), navController, authViewModel, selectedIndex)
    }
}
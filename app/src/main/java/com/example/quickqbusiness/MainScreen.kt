package com.example.quickqbusiness

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.quickqbusiness.model.NavItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quickqbusiness.ui.theme.PrimaryGreen40
import com.example.quickqbusiness.viewModel.AuthState
import com.example.quickqbusiness.viewModel.AuthViewModel
import com.example.quickqbusiness.viewModel.OrderViewModel
import com.example.quickqbusiness.viewModel.ShopViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    shopViewModel: ShopViewModel,
    orderViewModel: OrderViewModel
) {
    // From AppNavigation.kt
    val authState = authViewModel.authState.observeAsState()
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("signin") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false // false = white icons

    SideEffect {
        systemUiController.setStatusBarColor(
            color = PrimaryGreen40,
            darkIcons = useDarkIcons
        )
    }

    // separate nav controller for tabs
    val tabNavController = rememberNavController()


    // Observe the order list with IDs
    val orderListWithIds by orderViewModel.orderListWithIds.observeAsState(emptyList())

    // Calculate the pending size based on the size of the orderListWithIds
    val pendingSize by remember {
        derivedStateOf {
            orderListWithIds.size
        }
    }

    val navItemList = listOf(
        NavItem("pending", "Pending", R.drawable.pending, pendingSize),
        NavItem("accepted", "Accepted", R.drawable.accept, 0),
        NavItem("profile", "Profile", R.drawable.profile, 0)
    )

    val currentRoute by tabNavController.currentBackStackEntryAsState()
    val selectedRoute = currentRoute?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        bottomBar = {
            NavigationBar {
                navItemList.forEach { item ->
                    NavigationBarItem(
                        selected = selectedRoute == item.route,
                        onClick = {
                            if (selectedRoute != item.route) {
                                tabNavController.navigate(item.route) {
                                    popUpTo(tabNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (item.count > 0) {
                                    Badge { Text(item.count.toString()) }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(item.icon),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        ContentScreen(
            modifier = Modifier.padding(innerPadding),
            navController = tabNavController,
            authViewModel = authViewModel,
            shopViewModel = shopViewModel,
            orderViewModel = orderViewModel
        )
    }
}
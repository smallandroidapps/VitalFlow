package com.location.vitalflow

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.location.vitalflow.ui.screens.*
import com.location.vitalflow.ui.theme.VitalFlowTheme
import dagger.hilt.android.AndroidEntryPoint

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Water : Screen("water", "Water", Icons.Filled.LocalDrink)
    object Sleep : Screen("sleep", "Sleep", Icons.Filled.NightsStay)
    object Meal : Screen("meal", "Meal", Icons.Filled.Fastfood)
    object Profile : Screen("profile", "Profile", Icons.Filled.AccountCircle)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VitalFlowTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Water, Screen.Sleep, Screen.Meal, Screen.Profile)

                // Permission handling for Notifications
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) {}

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->
                                val isSelected = currentDestination?.hierarchy?.any { 
                                    it.route == screen.route || (screen == Screen.Water && it.route == "water_history")
                                } == true
                                
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.label) },
                                    selected = isSelected,
                                    onClick = {
                                        if (currentDestination?.route != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        } else if (screen == Screen.Water && currentDestination?.route == "water_history") {
                                            // Special case: if we are in water history and click Water tab, go back to main water UI
                                            navController.popBackStack(Screen.Water.route, inclusive = false)
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Water.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Water.route) {
                                val viewModel: WaterViewModel = hiltViewModel()
                                WaterScreen(viewModel, onNavigateToHistory = { navController.navigate("water_history") })
                            }
                            composable("water_history") {
                                val viewModel: WaterViewModel = hiltViewModel()
                                WaterHistoryScreen(viewModel)
                            }
                            composable(Screen.Sleep.route) {
                                val viewModel: SleepViewModel = hiltViewModel()
                                SleepScreen(viewModel)
                            }
                            composable(Screen.Meal.route) {
                                val viewModel: MealViewModel = hiltViewModel()
                                MealScreen(viewModel)
                            }
                            composable(Screen.Profile.route) {
                                val viewModel: ProfileViewModel = hiltViewModel()
                                ProfileScreen(viewModel)
                            }
                        }
                }
            }
        }
    }
}

package com.example.smartplantcare.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smart_plant_care.ui.navigation.Screen
import com.example.smart_plant_care.ui.screens.DetailsScreen
import com.example.smart_plant_care.ui.screens.EditScreen
import com.example.smart_plant_care.ui.screens.MyGardenScreen
import com.example.smart_plant_care.ui.screens.SearchScreen
import com.example.smart_plant_care.ui.screens.SettingsScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            if (currentRoute == Screen.MyGarden.route || currentRoute == Screen.Settings.route) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Garden") },
                        label = { Text("My Garden") },
                        selected = currentRoute == Screen.MyGarden.route,
                        onClick = {
                            navController.navigate(Screen.MyGarden.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Screen.MyGarden.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.MyGarden.route) { MyGardenScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route)})
            }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Details.route) { DetailsScreen() }
            composable(Screen.Edit.route) { EditScreen() }
        }
    }
}


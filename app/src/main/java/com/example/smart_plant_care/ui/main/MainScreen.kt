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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.repository.PlantRepository
import com.example.smart_plant_care.ui.navigation.Screen
import com.example.smart_plant_care.ui.screens.EditScreen
import com.example.smart_plant_care.ui.screens.MyGardenScreen
import com.example.smart_plant_care.ui.screens.SearchScreen
import com.example.smart_plant_care.ui.screens.SettingsScreen
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel
import com.example.smart_plant_care.ui.viewmodels.GardenViewModelFactory
import com.example.smartplantcare.ui.screens.DetailsScreen
import kotlinx.coroutines.launch


@Composable
fun MainScreen(repository: PlantRepository) {
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
            composable(Screen.MyGarden.route) {
                val viewModel: GardenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = GardenViewModelFactory(repository)
                )
                MyGardenScreen(viewModel = viewModel, onNavigateToSearch = { navController.navigate(Screen.Search.route)})
            }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onPlantClick = { plantId ->
                        if (plantId == -1) {
                            navController.navigate(Screen.Edit.createRoute("New Plant"))
                        } else {
                            navController.navigate(Screen.Details.createRoute(plantId))
                        }
                    }
                )
            }
            composable("details/{apiId}") { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("apiId")?.toIntOrNull() ?: 0
                DetailsScreen(
                    plantId = plantId,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = { speciesName ->
                        navController.navigate(Screen.Edit.createRoute(speciesName))
                    }
                )
            }
            composable(Screen.Edit.route) { backStackEntry ->
                val speciesName = backStackEntry.arguments?.getString("speciesName") ?: "Plant"

                val coroutineScope = rememberCoroutineScope()

                EditScreen(
                    speciesName = speciesName,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { customName, waterDays ->
                        val newPlant = MyPlantEntity(
                            customName = customName,
                            speciesName = speciesName,
                            waterIntervalDays = waterDays,
                            nextWateringDate = System.currentTimeMillis() + (waterDays * 24L * 60 * 60 * 1000)
                        )
                        coroutineScope.launch {
                            repository.insertPlant(newPlant)
                        }
                        navController.popBackStack(Screen.MyGarden.route, inclusive = false)
                    }
                )
            }
        }
    }
}



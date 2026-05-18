package com.example.smart_plant_care.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.repository.InsertPlantResult
import com.example.smart_plant_care.data.repository.PlantRepository
import com.example.smart_plant_care.notifications.PlantReminderScheduler
import com.example.smart_plant_care.ui.navigation.Screen
import com.example.smart_plant_care.ui.screens.EditScreen
import com.example.smart_plant_care.ui.screens.GardenPlantDetailsScreen
import com.example.smart_plant_care.ui.screens.MANUAL_ENTRY_SPECIES_TOKEN
import com.example.smart_plant_care.ui.screens.MyGardenScreen
import com.example.smart_plant_care.ui.screens.SearchScreen
import com.example.smart_plant_care.ui.screens.SettingsScreen
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel
import com.example.smart_plant_care.ui.viewmodels.GardenViewModelFactory
import com.example.smart_plant_care.ui.screens.DetailsScreen
import com.example.smart_plant_care.ui.viewmodels.SettingsViewModel
import com.example.smart_plant_care.ui.screens.PlantNotesScreen
import com.example.smart_plant_care.ui.screens.WateringHistoryScreen
import com.example.smart_plant_care.ui.screens.PlantDetailsEditScreen
import java.net.URLDecoder.decode
import kotlinx.coroutines.launch



@Composable
fun MainScreen(repository: PlantRepository, settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val gardenViewModel: GardenViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = GardenViewModelFactory(repository)
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val allPlants by gardenViewModel.plantsList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    var sharedPlantDetails by remember { mutableStateOf<com.example.smart_plant_care.data.remote.dto.PlantDetailsDto?>(null) }

    LaunchedEffect(settingsUiState.notificationsEnabled, allPlants) {
        if (settingsUiState.notificationsEnabled && PlantReminderScheduler.hasNotificationPermission(context)) {
            allPlants.forEach { plant -> PlantReminderScheduler.scheduleReminder(context, plant) }
        } else {
            allPlants.forEach { plant -> PlantReminderScheduler.cancelReminder(context, plant.id) }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute == Screen.MyGarden.route || currentRoute == Screen.Settings.route) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = stringResource(R.string.nav_cd_garden)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_my_garden)) },
                        selected = currentRoute == Screen.MyGarden.route,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(Screen.MyGarden.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = stringResource(R.string.nav_cd_settings)
                            )
                        },
                        label = { Text(stringResource(R.string.nav_settings)) },
                        selected = currentRoute == Screen.Settings.route,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
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
                MyGardenScreen(
                    viewModel = gardenViewModel,
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onDeletePlant = { plantId ->
                        gardenViewModel.deletePlant(plantId)
                        PlantReminderScheduler.cancelReminder(context, plantId)
                    },
                    onMarkPlantWatered = { plantId ->
                        gardenViewModel.markPlantAsWatered(plantId)
                    },
                    onEditPlant = { plantId ->
                        navController.navigate(Screen.EditExisting.createRoute(plantId))
                    },
                    onOpenPlantDetails = { plantId ->
                        navController.navigate(Screen.GardenDetails.createRoute(plantId))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(settingsViewModel = settingsViewModel)
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onBackClick = { navController.popBackStack() },
                    onPlantClick = { plantId ->
                        if (plantId == -1) {
                            sharedPlantDetails = null
                            navController.navigate(Screen.Edit.createRoute(MANUAL_ENTRY_SPECIES_TOKEN, 7))
                        } else {
                            navController.navigate(Screen.Details.createRoute(plantId))
                        }
                    }
                )
            }
            composable(Screen.Details.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("apiId")?.toIntOrNull() ?: 0
                DetailsScreen(
                    plantId = plantId,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = { detailsDto, defaultWaterDays ->
                        val newPlant = MyPlantEntity(
                            customName = detailsDto.commonName,
                            speciesName = detailsDto.commonName,
                            remotePlantId = detailsDto.id,
                            scientificName = detailsDto.scientificName?.firstOrNull(),
                            family = detailsDto.family,
                            origin = detailsDto.origin?.joinToString(),
                            plantType = detailsDto.type,
                            sunlight = detailsDto.sunlight?.joinToString(),
                            attracts = detailsDto.attracts?.joinToString(),
                            description = detailsDto.description,
                            waterIntervalDays = defaultWaterDays,
                            nextWateringDate = System.currentTimeMillis() + (defaultWaterDays * 24L * 60 * 60 * 1000),
                            fruitingSeason = detailsDto.fruitingSeason,
                            harvestSeason = detailsDto.harvestSeason,
                            harvestMethod = detailsDto.harvestMethod,
                            isMedicinal = detailsDto.medicinal,
                            isPoisonousToHumans = detailsDto.poisonousToHumans,
                            isPoisonousToPets = detailsDto.poisonousToPets,
                            imageUrl = detailsDto.defaultImage?.regularUrl ?: detailsDto.defaultImage?.thumbnail
                        )
                        gardenViewModel.insertPlant(newPlant) { result ->
                            when (result) {
                                InsertPlantResult.Added -> {
                                    navController.popBackStack(Screen.MyGarden.route, inclusive = false)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.getString(R.string.main_snackbar_added_to_garden)
                                        )
                                    }
                                }

                                InsertPlantResult.Duplicate -> {
                                    scope.launch {
                                        val snackbarResult = snackbarHostState.showSnackbar(
                                            message = context.getString(R.string.main_snackbar_duplicate),
                                            actionLabel = context.getString(R.string.main_snackbar_action_open_garden)
                                        )
                                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                                            val popped = navController.popBackStack(
                                                Screen.MyGarden.route,
                                                inclusive = false
                                            )
                                            if (!popped) {
                                                navController.navigate(Screen.MyGarden.route) {
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            }
            composable(Screen.Edit.route) { backStackEntry ->
                val rawName = backStackEntry.arguments?.getString("speciesName")
                    ?: context.getString(R.string.main_fallback_plant_name)
                val speciesName = decode(rawName, "UTF-8")
                val defaultWaterDays = backStackEntry.arguments?.getString("defaultWater")?.toIntOrNull() ?: 7

                EditScreen(
                    speciesName = speciesName,
                    defaultWaterDays = defaultWaterDays,
                    initialImageUrl = sharedPlantDetails?.defaultImage?.regularUrl
                        ?: sharedPlantDetails?.defaultImage?.thumbnail,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { customName, waterDays, imageUrl ->
                        val newPlant = MyPlantEntity(
                            customName = customName,
                            speciesName = if (speciesName == MANUAL_ENTRY_SPECIES_TOKEN) customName else speciesName,
                            scientificName = sharedPlantDetails?.scientificName?.firstOrNull(),
                            family = sharedPlantDetails?.family,
                            origin = sharedPlantDetails?.origin?.joinToString(),
                            plantType = sharedPlantDetails?.type,
                            sunlight = sharedPlantDetails?.sunlight?.joinToString(),
                            attracts = sharedPlantDetails?.attracts?.joinToString(),
                            description = sharedPlantDetails?.description,
                            waterIntervalDays = waterDays,
                            nextWateringDate = System.currentTimeMillis() + (waterDays * 24L * 60 * 60 * 1000),
                            fruitingSeason = sharedPlantDetails?.fruitingSeason,
                            harvestSeason = sharedPlantDetails?.harvestSeason,
                            harvestMethod = sharedPlantDetails?.harvestMethod,
                            isMedicinal = sharedPlantDetails?.medicinal,
                            isPoisonousToHumans = sharedPlantDetails?.poisonousToHumans,
                            isPoisonousToPets = sharedPlantDetails?.poisonousToPets,
                            imageUrl = imageUrl
                        )
                        gardenViewModel.insertPlant(newPlant)

                        sharedPlantDetails = null
                        navController.popBackStack(Screen.MyGarden.route, inclusive = false)
                    }
                )
            }
            composable(Screen.EditExisting.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull() ?: 0
                val plants by gardenViewModel.plantsList.collectAsState()
                val plantToEdit = plants.firstOrNull { it.id == plantId }

                if (plantToEdit != null) {
                    EditScreen(
                        speciesName = plantToEdit.speciesName,
                        defaultWaterDays = plantToEdit.waterIntervalDays,
                        initialCustomName = plantToEdit.customName,
                        initialImageUrl = plantToEdit.imageUrl,
                        isEditing = true,
                        onBackClick = { navController.popBackStack() },
                        onSaveClick = { customName, waterDays, imageUrl ->
                            val updatedPlant = plantToEdit.copy(
                                customName = customName,
                                waterIntervalDays = waterDays,
                                nextWateringDate = System.currentTimeMillis() + (waterDays * 24L * 60 * 60 * 1000),
                                imageUrl = imageUrl
                            )
                            gardenViewModel.updatePlant(updatedPlant)
                            navController.popBackStack()
                        }
                    )
                } else {
                    Text(stringResource(R.string.garden_details_not_found))
                }
            }
            composable(Screen.GardenDetails.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull() ?: 0
                val plants by gardenViewModel.plantsList.collectAsState()
                val plant = plants.firstOrNull { it.id == plantId }
                val recentEvents by gardenViewModel.recentWateringEvents(plantId).collectAsState(initial = emptyList())

                GardenPlantDetailsScreen(
                    plant = plant,
                    wateringEvents = recentEvents,
                    onBackClick = { navController.popBackStack() },
                    onOpenNotes = plant?.let { selectedPlant ->
                        {
                            navController.navigate(Screen.PlantNotes.createRoute(selectedPlant.id))
                        }
                    },
                    onEditDetails = plant?.let { selectedPlant ->
                        {
                            navController.navigate(Screen.PlantDetailsEdit.createRoute(selectedPlant.id))
                        }
                    },
                    onViewAllHistory = plant?.let { selectedPlant ->
                        {
                            navController.navigate(Screen.WateringHistory.createRoute(selectedPlant.id))
                        }
                    }
                )
            }
            composable(Screen.PlantNotes.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull() ?: 0
                val plants by gardenViewModel.plantsList.collectAsState()
                val plant = plants.firstOrNull { it.id == plantId }

                PlantNotesScreen(
                    plant = plant,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { text ->
                        if (plant != null) {
                            gardenViewModel.updatePlant(
                                plant.copy(
                                    noteText = text
                                )
                            )

                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.garden_details_notes_saved)
                                )
                            }

                            navController.popBackStack()
                        }
                    }
                )
            }
            composable(Screen.PlantDetailsEdit.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull() ?: 0
                val plants by gardenViewModel.plantsList.collectAsState()
                val plant = plants.firstOrNull { it.id == plantId }

                PlantDetailsEditScreen(
                    plant = plant,
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { updatedPlant ->
                        gardenViewModel.updatePlant(updatedPlant)
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.WateringHistory.route) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId")?.toIntOrNull() ?: 0
                val plants by gardenViewModel.plantsList.collectAsState()
                val plant = plants.firstOrNull { it.id == plantId }
                val wateringEvents by gardenViewModel
                    .allWateringEvents(plantId)
                    .collectAsState(initial = emptyList())

                WateringHistoryScreen(
                    plant = plant,
                    wateringEvents = wateringEvents,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

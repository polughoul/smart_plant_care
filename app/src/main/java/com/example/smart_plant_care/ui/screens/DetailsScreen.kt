package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.ui.viewmodels.DetailsUiState
import com.example.smart_plant_care.ui.viewmodels.DetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    plantId: Int,
    onBackClick: () -> Unit,
    onAddClick: (PlantDetailsDto, Int) -> Unit,
    detailsViewModel: DetailsViewModel = viewModel()
) {
    val uiState by detailsViewModel.uiState.collectAsState()

    LaunchedEffect(plantId) {
        detailsViewModel.loadPlantDetails(plantId)
    }

    val topBarTitle = when (val state = uiState) {
        is DetailsUiState.Success -> state.plant.commonName
        DetailsUiState.Loading -> "Loading..."
        else -> "Plant Details"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            val state = uiState
            if (state is DetailsUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val benchmark = state.plant.wateringBenchmark?.value
                        val days = benchmark?.split("-")?.firstOrNull()?.trim()?.toIntOrNull() ?: 7
                        onAddClick(state.plant, days)
                    },
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("Add to garden") }
                )
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            DetailsUiState.Loading, DetailsUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DetailsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = detailsViewModel::retry) {
                        Text("Retry")
                    }
                }
            }

            is DetailsUiState.Success -> {
                val plant = state.plant
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = plant.defaultImage?.regularUrl ?: plant.defaultImage?.thumbnail,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = plant.commonName, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = plant.scientificName?.firstOrNull() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val sunlightText = plant.sunlight?.joinToString()?.takeIf { it.isNotBlank() } ?: "Unknown"
                                val wateringText = plant.watering?.takeIf { it.isNotBlank() } ?: "Unknown"

                                Text("Sun heat: $sunlightText")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Water: $wateringText")
                            }
                        }
                    }
                }
            }
        }
    }
}
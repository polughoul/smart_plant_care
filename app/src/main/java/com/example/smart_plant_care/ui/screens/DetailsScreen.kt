package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.ui.viewmodels.DetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    plantId: Int,
    onBackClick: () -> Unit,
    onAddClick: (PlantDetailsDto, Int) -> Unit,
    detailsViewModel: DetailsViewModel = viewModel()
) {
    val details by detailsViewModel.plantDetails.collectAsState()
    val isLoading by detailsViewModel.isLoading.collectAsState()

    LaunchedEffect(plantId) {
        detailsViewModel.loadPlantDetails(plantId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(details?.commonName ?: "Загрузка...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Назад") }
                }
            )
        },
        floatingActionButton = {
            if (details != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val benchmark = details?.wateringBenchmark?.value
                        val days = benchmark?.split("-")?.firstOrNull()?.trim()?.toIntOrNull() ?: 7
                        onAddClick(details!!, days)
                    },
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("Add to garden") }
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (details != null) {
            val plant = details!!
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

                            Text("☀️ Sun heat: $sunlightText")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("💧 Water: $wateringText")
                        }
                    }
                }
            }
        }
    }
}
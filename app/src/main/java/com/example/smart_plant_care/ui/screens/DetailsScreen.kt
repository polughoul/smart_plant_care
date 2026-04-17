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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.WateringBenchmark
import com.example.smart_plant_care.ui.viewmodels.DetailsUiState
import com.example.smart_plant_care.ui.viewmodels.DetailsViewModel
import kotlin.math.roundToInt


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
                        val days = benchmarkToDefaultDays(state.plant.wateringBenchmark)
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
                var isDescriptionExpanded by remember(plant.id) { mutableStateOf(false) }
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

                        val quickFacts = buildList {
                            plant.family?.takeIf { it.isNotBlank() }?.let { add("Family: $it") }
                            plant.type?.takeIf { it.isNotBlank() }?.let { add("Type: $it") }
                            plant.origin?.takeIf { it.isNotEmpty() }?.joinToString()?.let { add("Origin: $it") }
                            plant.attracts?.takeIf { it.isNotEmpty() }?.joinToString()?.let { add("Attracts: $it") }
                            plant.fruitingSeason?.takeIf { it.isNotBlank() }?.let { add("Fruiting season: $it") }
                            plant.harvestSeason?.takeIf { it.isNotBlank() }?.let { add("Harvest season: $it") }
                            plant.harvestMethod?.takeIf { it.isNotBlank() }?.let { add("Harvest method: $it") }
                            plant.medicinal?.let { add("Medicinal: ${if (it) "Yes" else "No"}") }
                            plant.poisonousToHumans?.let { add("Poisonous to humans: ${if (it) "Yes" else "No"}") }
                            plant.poisonousToPets?.let { add("Poisonous to pets: ${if (it) "Yes" else "No"}") }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val guideSunlight = state.careSections
                                    .firstOrNull { it.type.equals("sunlight", ignoreCase = true) }
                                    ?.description
                                    ?.takeIf { it.isNotBlank() }
                                val sunlightText = plant.sunlight?.joinToString()?.takeIf { it.isNotBlank() }
                                    ?: guideSunlight
                                    ?: "Unknown"

                                val benchmarkText = plant.wateringBenchmark?.value?.takeIf { it.isNotBlank() }
                                val benchmarkLabel = formatBenchmarkLabel(plant.wateringBenchmark)
                                val guideWatering = state.careSections
                                    .firstOrNull { it.type.equals("watering", ignoreCase = true) }
                                    ?.description
                                    ?.takeIf { it.isNotBlank() }
                                val wateringText = plant.watering?.takeIf { it.isNotBlank() }
                                    ?: benchmarkText?.let { "Every $it days" }
                                    ?: guideWatering
                                    ?: "Unknown"
                                val waterLine = benchmarkLabel?.let { "$wateringText ($it)" } ?: wateringText

                                Text("Sunlight: $sunlightText")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Water: $waterLine")
                            }
                        }

                        if (quickFacts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Quick facts", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    quickFacts.forEachIndexed { index, fact ->
                                        if (index > 0) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                        }
                                        Text(text = fact, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Description", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))

                                val descriptionText = plant.description?.takeIf { it.isNotBlank() }

                                if (descriptionText != null) {
                                    Text(
                                        text = descriptionText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 4,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    TextButton(onClick = { isDescriptionExpanded = !isDescriptionExpanded }) {
                                        Text(if (isDescriptionExpanded) "Show less" else "Show more")
                                    }
                                } else {
                                    Text(
                                        text = "No description available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatBenchmarkLabel(benchmark: WateringBenchmark?): String? {
    val value = benchmark?.value?.takeIf { it.isNotBlank() } ?: return null
    val unit = benchmark.unit?.takeIf { it.isNotBlank() } ?: "days"
    return "$value $unit"
}

private fun benchmarkToDefaultDays(benchmark: WateringBenchmark?): Int {
    val raw = benchmark?.value?.takeIf { it.isNotBlank() } ?: return 7
    val numbers = Regex("\\d+").findAll(raw).map { it.value.toInt() }.toList()
    if (numbers.isEmpty()) return 7

    var days = numbers.average().roundToInt()
    val unit = benchmark.unit.orEmpty().lowercase()
    if ("week" in unit) {
        days *= 7
    } else if ("month" in unit) {
        days *= 30
    }
    return days.coerceIn(1, 30)
}


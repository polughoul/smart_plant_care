package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.WateringBenchmark
import com.example.smart_plant_care.data.remote.dto.bestImageUrl
import com.example.smart_plant_care.data.remote.dto.CareSectionDto
import com.example.smart_plant_care.ui.viewmodels.DetailsUiState
import com.example.smart_plant_care.ui.viewmodels.DetailsViewModel
import kotlin.math.roundToInt
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    plantId: Int,
    onBackClick: () -> Unit,
    onAddClick: (PlantDetailsDto, Int, List<CareSectionDto>) -> Unit,
    detailsViewModel: DetailsViewModel = viewModel()
) {
    val uiState by detailsViewModel.uiState.collectAsState()

    LaunchedEffect(plantId) {
        detailsViewModel.loadPlantDetails(plantId)
    }

    val topBarTitle = when (val state = uiState) {
        is DetailsUiState.Success -> state.plant.commonName
        DetailsUiState.Loading -> stringResource(R.string.details_title_loading)
        else -> stringResource(R.string.details_title_default)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.common_cd_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            val state = uiState
            if (state is DetailsUiState.Success) {
                ExtendedFloatingActionButton(
                    onClick = {
                        val days = benchmarkToDefaultDays(state.plant.wateringBenchmark)
                        onAddClick(state.plant, days, state.careSections)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = stringResource(R.string.common_cd_add)
                        )
                    },
                    text = { Text(stringResource(R.string.details_add_to_garden)) }
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
                        Text(stringResource(R.string.details_retry))
                    }
                }
            }
            is DetailsUiState.Success -> {
                val plant = state.plant
                var isDescriptionExpanded by remember(plant.id) { mutableStateOf(false) }
                val yesLabel = stringResource(R.string.common_yes)
                val noLabel = stringResource(R.string.common_no)
                val pruningCountLabel = plant.pruningCount?.let { count ->
                    val amount = count.amount
                    val interval = count.interval?.takeIf { it.isNotBlank() }
                    when {
                        amount != null && interval != null -> {
                            stringResource(R.string.fact_pruning_count_format, amount, interval)
                        }
                        amount != null -> {
                            stringResource(R.string.fact_pruning_count_amount_format, amount)
                        }
                        interval != null -> {
                            stringResource(R.string.fact_pruning_count_interval_format, interval)
                        }
                        else -> null
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = plant.defaultImage.bestImageUrl(),
                        contentDescription = stringResource(R.string.cd_plant_photo_format, plant.commonName),
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
                            plant.family?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_family_format, it))
                            }
                            plant.type?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_type_format, it))
                            }
                            plant.origin?.takeIf { it.isNotEmpty() }?.joinToString()?.let {
                                add(stringResource(R.string.fact_origin_format, it))
                            }
                            plant.attracts?.takeIf { it.isNotEmpty() }?.joinToString()?.let {
                                add(stringResource(R.string.fact_attracts_format, it))
                            }
                            plant.fruitingSeason?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_fruiting_season_format, it))
                            }
                            plant.harvestSeason?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_harvest_season_format, it))
                            }
                            plant.harvestMethod?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_harvest_method_format, it))
                            }
                            plant.medicinal?.let {
                                add(stringResource(R.string.fact_medicinal_format, if (it) yesLabel else noLabel))
                            }
                            plant.poisonousToHumans?.let {
                                add(stringResource(R.string.fact_poisonous_humans_format, if (it) yesLabel else noLabel))
                            }
                            plant.poisonousToPets?.let {
                                add(stringResource(R.string.fact_poisonous_pets_format, if (it) yesLabel else noLabel))
                            }
                            plant.pruningMonths?.takeIf { it.isNotEmpty() }?.joinToString()?.let {
                                add(stringResource(R.string.fact_pruning_months_format, it))
                            }
                            pruningCountLabel?.let {
                                add(it)
                            }
                            plant.growthRate?.takeIf { it.isNotBlank() }?.let {
                                add(stringResource(R.string.fact_growth_rate_format, it))
                            }
                            plant.soil?.takeIf { it.isNotEmpty() }?.joinToString()?.let {
                                add(stringResource(R.string.fact_soil_format, it))
                            }
                            plant.rare?.let {
                                add(stringResource(R.string.fact_rare_format, if (it) yesLabel else noLabel))
                            }
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
                                    ?: stringResource(R.string.details_unknown)
                                val benchmarkText = plant.wateringBenchmark?.value?.takeIf { it.isNotBlank() }
                                val benchmarkLabel = formatBenchmarkLabel(plant.wateringBenchmark)
                                val guideWatering = state.careSections
                                    .firstOrNull { it.type.equals("watering", ignoreCase = true) }
                                    ?.description
                                    ?.takeIf { it.isNotBlank() }
                                val wateringText = plant.watering?.takeIf { it.isNotBlank() }
                                    ?: benchmarkText?.let { stringResource(R.string.details_every_days_format, it) }
                                    ?: guideWatering
                                    ?: stringResource(R.string.details_unknown)
                                val waterLine = benchmarkLabel?.let { "$wateringText ($it)" } ?: wateringText

                                Text(stringResource(R.string.details_sunlight_line, sunlightText))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(stringResource(R.string.details_water_line, waterLine))
                            }
                        }
                        if (quickFacts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = stringResource(R.string.details_quick_facts),
                                        style = MaterialTheme.typography.titleMedium
                                    )
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

                        val careGuideSections = state.careSections
                            .mapNotNull { section ->
                                val type = section.type?.trim()?.lowercase()
                                val description = section.description?.trim().takeIf { !it.isNullOrBlank() }
                                if (type == null || description == null) null else type to description
                            }
                        if (careGuideSections.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            careGuideSections.forEach { (type, description) ->
                                CareGuideCard(type = type, description = description)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.details_description),
                                    style = MaterialTheme.typography.titleMedium
                                )
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
                                        Text(
                                            if (isDescriptionExpanded) {
                                                stringResource(R.string.details_show_less)
                                            } else {
                                                stringResource(R.string.details_show_more)
                                            }
                                        )
                                    }
                                } else {
                                    Text(
                                        text = stringResource(R.string.details_no_description),
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

@Composable
private fun CareGuideCard(type: String, description: String) {
    var isExpanded by remember(type) { mutableStateOf(false) }
    val title = stringResource(
        when (type) {
            "watering" -> R.string.care_section_watering
            "sunlight" -> R.string.care_section_sunlight
            "pruning" -> R.string.care_section_pruning
            else -> R.string.care_section_generic
        }
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis
            )
            if (description.length > 200 || description.lines().size > 4) {
                TextButton(onClick = { isExpanded = !isExpanded }) {
                    Text(
                        if (isExpanded) {
                            stringResource(R.string.details_show_less)
                        } else {
                            stringResource(R.string.details_show_more)
                        }
                    )
                }
            }
        }
    }
}

package com.example.smart_plant_care.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Delete
import com.example.smart_plant_care.R
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@Composable
fun PlantCard(
    name: String,
    status: String,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        onClick = onCardClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column{
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.my_garden_cd_edit)
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.my_garden_cd_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGardenScreen(
    viewModel: GardenViewModel,
    onNavigateToSearch: () -> Unit,
    onDeletePlant: (Int) -> Unit,
    onEditPlant: (Int) -> Unit,
    onOpenPlantDetails: (Int) -> Unit
) {

    val plants by viewModel.plantsList.collectAsState()
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlants = remember(plants, searchQuery) {
        if (searchQuery.isBlank()) {
            plants
        } else {
            val query = searchQuery.trim().lowercase()
            plants.filter { plant ->
                plant.customName.lowercase().contains(query) ||
                    plant.speciesName.lowercase().contains(query)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_garden_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSearch) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.my_garden_fab_cd))
            }
        }
    ) { paddingValues ->

        if (plants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.my_garden_empty))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    label = { Text(stringResource(R.string.my_garden_search_label)) },
                    placeholder = { Text(stringResource(R.string.my_garden_search_placeholder)) },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredPlants.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.my_garden_no_matches))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPlants) { plant ->
                            PlantCard(
                                name = plant.customName,
                                status = calculateDaysRemaining(context, plant.nextWateringDate),
                                onCardClick = { onOpenPlantDetails(plant.id) },
                                onEditClick = { onEditPlant(plant.id) },
                                onDeleteClick = { onDeletePlant(plant.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

fun calculateDaysRemaining(context: Context, nextWateringMillis: Long): String {
    val now = System.currentTimeMillis()
    if (nextWateringMillis <= now) return context.getString(R.string.watering_status_due_now)

    val zoneId = ZoneId.systemDefault()
    val today = Instant.ofEpochMilli(now).atZone(zoneId).toLocalDate()
    val nextDate = Instant.ofEpochMilli(nextWateringMillis).atZone(zoneId).toLocalDate()
    val daysUntil = ChronoUnit.DAYS.between(today, nextDate).toInt()

    return when {
        daysUntil <= 0 -> context.getString(R.string.watering_status_today)
        daysUntil == 1 -> context.getString(R.string.watering_status_tomorrow)
        else -> context.resources.getQuantityString(
            R.plurals.watering_status_after_days,
            daysUntil,
            daysUntil
        )
    }
}

package com.example.smart_plant_care.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Delete
import com.example.smart_plant_care.R
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantCard(
    modifier: Modifier = Modifier,
    name: String,
    status: String,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onLongPress: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 180),
        label = "plantCardContainerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        },
        animationSpec = tween(durationMillis = 180),
        label = "plantCardBorderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onToggleSelection() else onCardClick()
                },
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
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
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null
                )
            } else {
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
    var selectedPlantIds by remember { mutableStateOf(setOf<Int>()) }
    var pendingDeleteIds by remember { mutableStateOf(setOf<Int>()) }
    val isSelectionMode = selectedPlantIds.isNotEmpty()

    LaunchedEffect(plants) {
        val existingIds = plants.map { it.id }.toSet()
        selectedPlantIds = selectedPlantIds.intersect(existingIds)
        pendingDeleteIds = pendingDeleteIds.intersect(existingIds)
    }

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
                title = {
                    AnimatedContent(
                        targetState = selectedPlantIds.size to isSelectionMode,
                        label = "myGardenTopBarTitle"
                    ) { (count, selectionMode) ->
                        if (selectionMode) {
                            Text(stringResource(R.string.my_garden_selected_count, count))
                        } else {
                            Text(stringResource(R.string.my_garden_title))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(onClick = onNavigateToSearch) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.my_garden_fab_cd))
                }
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

                AnimatedVisibility(
                    visible = isSelectionMode,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.my_garden_selected_count, selectedPlantIds.size),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                TextButton(
                                    onClick = { pendingDeleteIds = selectedPlantIds },
                                    enabled = selectedPlantIds.isNotEmpty()
                                ) {
                                    Text(stringResource(R.string.delete_dialog_confirm))
                                }
                                TextButton(onClick = { selectedPlantIds = emptySet() }) {
                                    Text(stringResource(R.string.my_garden_cd_clear_selection))
                                }
                            }
                        }
                    }
                }

                if (isSelectionMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

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
                                modifier = Modifier.animateItem(),
                                name = plant.customName,
                                status = calculateDaysRemaining(context, plant.nextWateringDate),
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedPlantIds.contains(plant.id),
                                onCardClick = { onOpenPlantDetails(plant.id) },
                                onToggleSelection = {
                                    selectedPlantIds = if (selectedPlantIds.contains(plant.id)) {
                                        selectedPlantIds - plant.id
                                    } else {
                                        selectedPlantIds + plant.id
                                    }
                                },
                                onLongPress = {
                                    selectedPlantIds = selectedPlantIds + plant.id
                                },
                                onEditClick = { onEditPlant(plant.id) },
                                onDeleteClick = { pendingDeleteIds = setOf(plant.id) }
                            )
                        }
                    }
                }
            }
        }

        if (pendingDeleteIds.isNotEmpty()) {
            val selectedNames = plants.filter { pendingDeleteIds.contains(it.id) }.map { it.customName }
            val message = if (pendingDeleteIds.size == 1) {
                stringResource(
                    R.string.delete_dialog_message_single,
                    selectedNames.firstOrNull().orEmpty()
                )
            } else {
                pluralStringResource(
                    R.plurals.delete_dialog_message_multiple,
                    pendingDeleteIds.size,
                    pendingDeleteIds.size
                )
            }

            AlertDialog(
                onDismissRequest = { pendingDeleteIds = emptySet() },
                title = { Text(stringResource(R.string.delete_dialog_title)) },
                text = { Text(message) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            pendingDeleteIds.forEach { onDeletePlant(it) }
                            selectedPlantIds = selectedPlantIds - pendingDeleteIds
                            pendingDeleteIds = emptySet()
                        }
                    ) {
                        Text(stringResource(R.string.delete_dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingDeleteIds = emptySet() }) {
                        Text(stringResource(R.string.delete_dialog_cancel))
                    }
                }
            )
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

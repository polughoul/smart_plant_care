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
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.snap
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.preferences.GardenSortOption
import com.example.smart_plant_care.data.preferences.UserPreferences
import com.example.smart_plant_care.data.preferences.UserPreferencesDataStore
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel
import coil.compose.AsyncImage
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.compose.material3.MenuAnchorType

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlantCard(
    modifier: Modifier = Modifier,
    imageModel: String?,
    name: String,
    status: String,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    isActionsRevealed: Boolean,
    onRevealChanged: (Boolean) -> Unit,
    onCardClick: () -> Unit,
    onToggleSelection: () -> Unit,
    onLongPress: () -> Unit,
    onWaterClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val selectedLabel = stringResource(R.string.cd_selected_checkbox)
    val density = LocalDensity.current
    val revealActionsWidth = 176.dp
    val revealActionsWidthPx = with(density) { revealActionsWidth.toPx() }
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val contentOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = if (isDragging) snap() else tween(durationMillis = 180),
        label = "plantCardSwipeOffset"
    )

    LaunchedEffect(isActionsRevealed, revealActionsWidthPx) {
        if (!isDragging) {
            dragOffset = if (isActionsRevealed) -revealActionsWidthPx else 0f
        }
    }

    val containerColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
            isActionsRevealed -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(durationMillis = 180),
        label = "plantCardContainerColor"
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isActionsRevealed -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        },
        animationSpec = tween(durationMillis = 180),
        label = "plantCardBorderColor"
    )

    if (isSelectionMode) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onToggleSelection,
                    onLongClick = onLongPress
                ),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            PlantCardContent(
                imageModel = imageModel,
                name = name,
                status = status,
                trailingContent = {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null,
                        modifier = Modifier.semantics {
                            contentDescription = selectedLabel
                        }
                    )
                }
            )
        }
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .semantics { stateDescription = status },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onWaterClick) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.cd_action_mark_watered),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_action_edit)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_action_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(contentOffset.roundToInt(), 0) }
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                            isDragging = true
                            dragOffset = (dragOffset + delta).coerceIn(-revealActionsWidthPx, 0f)
                        },
                        onDragStarted = {
                            isDragging = true
                            dragOffset = contentOffset
                        },
                        onDragStopped = { velocity ->
                            val revealThreshold = -revealActionsWidthPx * 0.45f
                            val revealByPosition = dragOffset <= revealThreshold
                            val revealByVelocity = velocity < -900f
                            val closeByVelocity = velocity > 900f
                            val shouldReveal = (revealByPosition || revealByVelocity) && !closeByVelocity
                            isDragging = false
                            dragOffset = if (shouldReveal) -revealActionsWidthPx else 0f
                            onRevealChanged(shouldReveal)
                        }
                    )
                    .combinedClickable(
                        onClick = {
                            if (isActionsRevealed) {
                                onRevealChanged(false)
                            } else {
                                onCardClick()
                            }
                        },
                        onLongClick = onLongPress
                    ),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                border = BorderStroke(1.dp, borderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                PlantCardContent(
                    imageModel = imageModel,
                    name = name,
                    status = status
                )
            }
        }
    }
}

@Composable
private fun PlantCardContent(
    imageModel: String?,
    name: String,
    status: String,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!imageModel.isNullOrBlank()) {
            AsyncImage(
                model = imageModel,
                contentDescription = stringResource(R.string.cd_plant_photo_format, name),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
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
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = status,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        trailingContent?.invoke()
    }
}

@Composable
private fun PulsingAddFab(
    onClick: () -> Unit
) {
    val pulseColor = MaterialTheme.colorScheme.primary
    val infinite = rememberInfiniteTransition(label = "addFabPulse")
    val pulseScale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infinite.animateFloat(
        initialValue = 0.30f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                    alpha = pulseAlpha
                }
                .background(pulseColor.copy(alpha = 0.45f), CircleShape)
        )
        FloatingActionButton(
            onClick = onClick,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.my_garden_fab_cd))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGardenScreen(
    viewModel: GardenViewModel,
    onNavigateToSearch: () -> Unit,
    onDeletePlant: (Int) -> Unit,
    onMarkPlantWatered: (Int) -> Unit,
    onEditPlant: (Int) -> Unit,
    onOpenPlantDetails: (Int) -> Unit
) {

    val plants by viewModel.plantsList.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val preferencesStore = remember { UserPreferencesDataStore(context) }
    val preferences by preferencesStore.preferencesFlow.collectAsState(initial = UserPreferences())
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlantIds by remember { mutableStateOf(setOf<Int>()) }
    var pendingDeleteIds by remember { mutableStateOf(setOf<Int>()) }
    var revealedPlantId by remember { mutableStateOf<Int?>(null) }
    val sortOption = preferences.gardenSortOption
    var sortExpanded by remember { mutableStateOf(false) }
    val isSelectionMode = selectedPlantIds.isNotEmpty()

    LaunchedEffect(plants) {
        val existingIds = plants.map { it.id }.toSet()
        selectedPlantIds = selectedPlantIds.intersect(existingIds)
        pendingDeleteIds = pendingDeleteIds.intersect(existingIds)
        if (revealedPlantId != null && !existingIds.contains(revealedPlantId)) {
            revealedPlantId = null
        }
    }

    LaunchedEffect(isSelectionMode) {
        if (isSelectionMode) {
            revealedPlantId = null
        }
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
    val sortedPlants = remember(filteredPlants, sortOption) {
        when (sortOption) {
            GardenSortOption.NEXT_WATERING -> filteredPlants.sortedWith(
                compareBy<com.example.smart_plant_care.data.local.entity.MyPlantEntity> { it.nextWateringDate }
                    .thenBy { it.customName.lowercase() }
            )
            GardenSortOption.NAME -> filteredPlants.sortedWith(
                compareBy<com.example.smart_plant_care.data.local.entity.MyPlantEntity> { it.customName.lowercase() }
                    .thenBy { it.nextWateringDate }
            )
        }
    }
    val selectedCountLabel = stringResource(R.string.cd_selected_count_format, selectedPlantIds.size)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.my_garden_title),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                PulsingAddFab(onClick = onNavigateToSearch)
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

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ExposedDropdownMenuBox(
                        expanded = sortExpanded,
                        onExpandedChange = { sortExpanded = !sortExpanded }
                    ) {
                        OutlinedTextField(
                            value = stringResource(sortOption.labelRes),
                            onValueChange = {},
                            readOnly = true,
                            singleLine = true,
                            label = { Text(stringResource(R.string.my_garden_sort_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            GardenSortOption.values().forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(option.labelRes)) },
                                    onClick = {
                                        scope.launch { preferencesStore.setGardenSortOption(option) }
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = isSelectionMode,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    SelectionActionPanel(
                        selectedCountLabel = selectedCountLabel,
                        selectedCount = selectedPlantIds.size,
                        onMarkWatered = {
                            val idsToWater = selectedPlantIds.toList()
                            idsToWater.forEach(onMarkPlantWatered)
                            selectedPlantIds = emptySet()
                        },
                        onDeleteSelected = { pendingDeleteIds = selectedPlantIds },
                        onClearSelection = { selectedPlantIds = emptySet() }
                    )
                }

                if (isSelectionMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (sortedPlants.isEmpty()) {
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
                        items(sortedPlants) { plant ->
                            PlantCard(
                                modifier = Modifier.animateItem(),
                                imageModel = plant.imageUrl,
                                name = plant.customName,
                                status = calculateDaysRemaining(context, plant.nextWateringDate),
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedPlantIds.contains(plant.id),
                                isActionsRevealed = revealedPlantId == plant.id,
                                onRevealChanged = { reveal ->
                                    revealedPlantId = if (reveal) plant.id else revealedPlantId?.takeUnless { it == plant.id }
                                },
                                onCardClick = {
                                    revealedPlantId = null
                                    onOpenPlantDetails(plant.id)
                                },
                                onToggleSelection = {
                                    revealedPlantId = null
                                    selectedPlantIds = if (selectedPlantIds.contains(plant.id)) {
                                        selectedPlantIds - plant.id
                                    } else {
                                        selectedPlantIds + plant.id
                                    }
                                },
                                onLongPress = {
                                    revealedPlantId = null
                                    selectedPlantIds = selectedPlantIds + plant.id
                                },
                                onWaterClick = {
                                    revealedPlantId = null
                                    onMarkPlantWatered(plant.id)
                                },
                                onEditClick = {
                                    revealedPlantId = null
                                    onEditPlant(plant.id)
                                },
                                onDeleteClick = {
                                    revealedPlantId = null
                                    pendingDeleteIds = setOf(plant.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (pendingDeleteIds.isNotEmpty()) {
            val deletedPlants = plants.filter { pendingDeleteIds.contains(it.id) }
            val selectedNames = deletedPlants.map { it.customName }
            val deletedMessage = if (deletedPlants.size == 1) {
                stringResource(
                    R.string.my_garden_deleted_single,
                    deletedPlants.first().customName
                )
            } else {
                pluralStringResource(
                    R.plurals.my_garden_deleted_multiple,
                    deletedPlants.size,
                    deletedPlants.size
                )
            }
            val undoLabel = stringResource(R.string.my_garden_undo)
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
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = deletedMessage,
                                    actionLabel = undoLabel
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    deletedPlants.forEach { plant ->
                                        viewModel.insertPlant(plant)
                                    }
                                }
                            }
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

@Composable
private fun SelectionActionPanel(
    selectedCountLabel: String,
    selectedCount: Int,
    onMarkWatered: () -> Unit,
    onDeleteSelected: () -> Unit,
    onClearSelection: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .semantics { stateDescription = selectedCountLabel },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.my_garden_selected_count, selectedCount),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onMarkWatered, enabled = selectedCount > 0) {
                    Text(stringResource(R.string.my_garden_mark_watered))
                }
                TextButton(onClick = onDeleteSelected, enabled = selectedCount > 0) {
                    Text(stringResource(R.string.delete_dialog_confirm))
                }
                TextButton(onClick = onClearSelection) {
                    Text(stringResource(R.string.my_garden_cd_clear_selection))
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

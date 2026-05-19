package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenPlantDetailsScreen(
    plant: MyPlantEntity?,
    wateringEvents: List<WateringEventEntity> = emptyList(),
    onBackClick: () -> Unit,
    onOpenNotes: (() -> Unit)? = null,
    onEditDetails: (() -> Unit)? = null,
    onViewAllHistory: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = plant?.customName ?: stringResource(R.string.garden_details_default_title),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.common_cd_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        if (plant == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.garden_details_not_found))
            }
            return@Scaffold
        }

        var isNoteExpanded by remember(plant.id, plant.noteText) {
            mutableStateOf(false)
        }

        var isDescriptionExpanded by remember(plant.id, plant.description) {
            mutableStateOf(false)
        }

        val yesLabel = stringResource(R.string.common_yes)
        val noLabel = stringResource(R.string.common_no)
        val pruningCountLabel = run {
            val amount = plant.pruningCountAmount
            val interval = plant.pruningCountInterval?.takeIf { it.isNotBlank() }
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

        val wateringIntervalFact = pluralStringResource(
            R.plurals.fact_water_every_days,
            plant.waterIntervalDays,
            plant.waterIntervalDays
        )

        val facts = buildList {
            plant.speciesName?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_species_format, it))
            }

            plant.scientificName?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_scientific_name_format, it))
            }

            plant.family?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_family_format, it))
            }

            plant.plantType?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_type_format, it))
            }

            plant.origin?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_origin_format, it))
            }

            plant.sunlight?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_sunlight_format, it))
            }

            plant.attracts?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_attracts_format, it))
            }

            plant.pruningMonths?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_pruning_months_format, it))
            }

            pruningCountLabel?.let {
                add(it)
            }

            plant.growthRate?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_growth_rate_format, it))
            }

            plant.soil?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_soil_format, it))
            }

            plant.rare?.let {
                add(stringResource(R.string.fact_rare_format, if (it) yesLabel else noLabel))
            }

            add(wateringIntervalFact)

            plant.isMedicinal?.let {
                add(stringResource(R.string.fact_medicinal_format, if (it) yesLabel else noLabel))
            }

            plant.isPoisonousToHumans?.let {
                add(stringResource(R.string.fact_poisonous_humans_format, if (it) yesLabel else noLabel))
            }

            plant.isPoisonousToPets?.let {
                add(stringResource(R.string.fact_poisonous_pets_format, if (it) yesLabel else noLabel))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PlantDetailsHero(plant = plant)

            DetailsSectionCard(
                title = stringResource(R.string.garden_details_section_details),
                actionText = stringResource(R.string.common_edit),
                onActionClick = onEditDetails
            ) {
                facts.forEachIndexed { index, fact ->
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(
                        text = fact,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            DetailsSectionCard(
                title = stringResource(R.string.details_description),
                actionText = stringResource(R.string.common_edit),
                onActionClick = onEditDetails
            ) {
                val descriptionText = plant.description?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.details_no_description)

                Text(
                    text = descriptionText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 6,
                    overflow = TextOverflow.Ellipsis
                )

                if (plant.description?.isNotBlank() == true) {
                    val canExpand = descriptionText.length > 200 || descriptionText.lines().size > 6
                    if (canExpand) {
                        TextButton(onClick = { isDescriptionExpanded = !isDescriptionExpanded }) {
                            Text(
                                text = if (isDescriptionExpanded) {
                                    stringResource(R.string.details_show_less)
                                } else {
                                    stringResource(R.string.details_show_more)
                                }
                            )
                        }
                    }
                }
            }

            val careGuideSections = listOf(
                stringResource(R.string.care_section_watering) to plant.careGuideWatering,
                stringResource(R.string.care_section_sunlight) to plant.careGuideSunlight,
                stringResource(R.string.care_section_pruning) to plant.careGuidePruning
            ).filter { it.second?.isNotBlank() == true }

            careGuideSections.forEach { (title, description) ->
                CareGuideSectionCard(title = title, description = description.orEmpty())
            }

            DetailsSectionCard(
                title = stringResource(R.string.garden_details_notes_title),
                actionText = stringResource(
                    if (plant.noteText.isNullOrBlank()) {
                        R.string.common_add
                    } else {
                        R.string.common_edit
                    }
                ),
                onActionClick = onOpenNotes
            ) {
                val noteText = plant.noteText?.takeIf { it.isNotBlank() }

                if (noteText == null) {
                    Text(
                        text = stringResource(R.string.garden_details_notes_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = noteText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isNoteExpanded) Int.MAX_VALUE else 4,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (noteText.length > 160 || noteText.lines().size > 4) {
                        TextButton(
                            onClick = { isNoteExpanded = !isNoteExpanded }
                        ) {
                            Text(
                                text = stringResource(
                                    if (isNoteExpanded) {
                                        R.string.details_show_less
                                    } else {
                                        R.string.details_show_more
                                    }
                                )
                            )
                        }
                    }
                }
            }

            DetailsSectionCard(
                title = stringResource(R.string.garden_details_history_title),
                actionText = stringResource(R.string.garden_details_history_view_all),
                onActionClick = onViewAllHistory
            ) {
                if (wateringEvents.isEmpty()) {
                    Text(
                        text = stringResource(R.string.garden_details_history_empty),
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    wateringEvents.forEachIndexed { index, event ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        val formattedDate = formatWateredDate(event.wateredAt)
                        Text(
                            text = stringResource(R.string.garden_details_history_item, formattedDate),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CareGuideSectionCard(title: String, description: String) {
    var isExpanded by remember(title) { mutableStateOf(false) }
    DetailsSectionCard(title = title) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis
        )
        if (description.length > 200 || description.lines().size > 4) {
            TextButton(onClick = { isExpanded = !isExpanded }) {
                Text(
                    text = stringResource(
                        if (isExpanded) {
                            R.string.details_show_less
                        } else {
                            R.string.details_show_more
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun PlantDetailsHero(
    plant: MyPlantEntity
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        if (!plant.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = plant.imageUrl,
                contentDescription = stringResource(R.string.cd_plant_photo_format, plant.customName),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.flower_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(96.dp)
                    .alpha(0.85f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp)
        ) {
            Text(
                text = plant.customName,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            if (plant.speciesName?.isNotBlank() == true) {
                Text(
                    text = plant.speciesName.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DetailsSectionCard(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                if (actionText != null && onActionClick != null) {
                    TextButton(onClick = onActionClick) {
                        Text(actionText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

private fun formatWateredDate(epochMillis: Long): String {
    val date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

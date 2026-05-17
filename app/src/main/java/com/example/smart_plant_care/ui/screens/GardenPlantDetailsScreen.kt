package com.example.smart_plant_care.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenPlantDetailsScreen(
    plant: MyPlantEntity?,
    wateringEvents: List<WateringEventEntity> = emptyList(),
    onBackClick: () -> Unit,
    onMarkWateredClick: (() -> Unit)? = null,
    onTestReminderIn5Seconds: (() -> Unit)? = null,
    onOpenNotes: (() -> Unit)? = null,
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

        val yesLabel = stringResource(R.string.common_yes)
        val noLabel = stringResource(R.string.common_no)
        val wateringIntervalFact = pluralStringResource(
            R.plurals.fact_water_every_days,
            plant.waterIntervalDays,
            plant.waterIntervalDays
        )
        val facts = buildList {
            add(stringResource(R.string.fact_species_format, plant.speciesName))
            plant.scientificName?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_scientific_name_format, it))
            }
            plant.family?.takeIf { it.isNotBlank() }?.let { add(stringResource(R.string.fact_family_format, it)) }
            plant.plantType?.takeIf { it.isNotBlank() }?.let { add(stringResource(R.string.fact_type_format, it)) }
            plant.origin?.takeIf { it.isNotBlank() }?.let { add(stringResource(R.string.fact_origin_format, it)) }
            plant.sunlight?.takeIf { it.isNotBlank() }?.let { add(stringResource(R.string.fact_sunlight_format, it)) }
            plant.attracts?.takeIf { it.isNotBlank() }?.let { add(stringResource(R.string.fact_attracts_format, it)) }
            plant.fruitingSeason?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_fruiting_season_format, it))
            }
            plant.harvestSeason?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_harvest_season_format, it))
            }
            plant.harvestMethod?.takeIf { it.isNotBlank() }?.let {
                add(stringResource(R.string.fact_harvest_method_format, it))
            }
            add(wateringIntervalFact)
            add(stringResource(R.string.fact_medicinal_format, if (plant.isMedicinal) yesLabel else noLabel))
            add(stringResource(R.string.fact_poisonous_humans_format, if (plant.isPoisonousToHumans) yesLabel else noLabel))
            add(stringResource(R.string.fact_poisonous_pets_format, if (plant.isPoisonousToPets) yesLabel else noLabel))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!plant.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = stringResource(R.string.cd_plant_photo_format, plant.customName),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = plant.customName, style = MaterialTheme.typography.headlineSmall)
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.garden_details_section_details), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    facts.forEachIndexed { index, fact ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        Text(text = fact, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.details_description),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = plant.description?.takeIf { it.isNotBlank() }
                            ?: stringResource(R.string.details_no_description),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.garden_details_notes_title),
                            style = MaterialTheme.typography.titleMedium
                        )

                        TextButton(
                            onClick = { onOpenNotes?.invoke() },
                            enabled = onOpenNotes != null
                        ) {
                            Text(
                                text = if (plant.noteText.isNullOrBlank()) {
                                    "Add"
                                } else {
                                    "Edit"
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = plant.noteText?.takeIf { it.isNotBlank() } ?: "No notes yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.garden_details_history_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(
                            onClick = { onViewAllHistory?.invoke() },
                            enabled = onViewAllHistory != null
                        ) {
                            Text(stringResource(R.string.garden_details_history_view_all))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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

            if (onMarkWateredClick != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.garden_details_section_actions),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onMarkWateredClick) {
                            Text(stringResource(R.string.garden_details_mark_watered_now))
                        }
                    }
                }
            }

            if (onTestReminderIn5Seconds != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stringResource(R.string.garden_details_section_reminder_test), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onTestReminderIn5Seconds) {
                            Text(stringResource(R.string.garden_details_test_button))
                        }
                    }
                }
            }
        }
    }
}

private fun formatWateredDate(epochMillis: Long): String {
    val date = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    return formatter.format(date)
}

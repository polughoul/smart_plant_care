package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.local.entity.MyPlantEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GardenPlantDetailsScreen(
    plant: MyPlantEntity?,
    onBackClick: () -> Unit,
    onTestReminderIn5Seconds: (() -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(plant?.customName ?: stringResource(R.string.garden_details_default_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

        val facts = buildList {
            add("Species: ${plant.speciesName}")
            plant.scientificName?.takeIf { it.isNotBlank() }?.let { add("Scientific name: $it") }
            plant.family?.takeIf { it.isNotBlank() }?.let { add("Family: $it") }
            plant.plantType?.takeIf { it.isNotBlank() }?.let { add("Type: $it") }
            plant.origin?.takeIf { it.isNotBlank() }?.let { add("Origin: $it") }
            plant.sunlight?.takeIf { it.isNotBlank() }?.let { add("Sunlight: $it") }
            plant.attracts?.takeIf { it.isNotBlank() }?.let { add("Attracts: $it") }
            plant.fruitingSeason?.takeIf { it.isNotBlank() }?.let { add("Fruiting season: $it") }
            plant.harvestSeason?.takeIf { it.isNotBlank() }?.let { add("Harvest season: $it") }
            plant.harvestMethod?.takeIf { it.isNotBlank() }?.let { add("Harvest method: $it") }
            add("Water every ${plant.waterIntervalDays} days")
            add("Medicinal: ${if (plant.isMedicinal) "Yes" else "No"}")
            add("Poisonous to humans: ${if (plant.isPoisonousToHumans) "Yes" else "No"}")
            add("Poisonous to pets: ${if (plant.isPoisonousToPets) "Yes" else "No"}")
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
                    contentDescription = plant.customName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = plant.customName, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.garden_details_readonly_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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


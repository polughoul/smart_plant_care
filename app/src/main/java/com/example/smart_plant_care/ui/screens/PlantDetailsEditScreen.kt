package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.local.entity.MyPlantEntity


private const val SHORT_FIELD_MAX_LENGTH = 80
private const val DESCRIPTION_MAX_LENGTH = 1000


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsEditScreen(
    plant: MyPlantEntity?,
    onBackClick: () -> Unit,
    onSaveClick: (MyPlantEntity) -> Unit
) {
    var speciesName by rememberSaveable(plant?.id) { mutableStateOf(plant?.speciesName.orEmpty()) }
    var scientificName by rememberSaveable(plant?.id) { mutableStateOf(plant?.scientificName.orEmpty()) }
    var family by rememberSaveable(plant?.id) { mutableStateOf(plant?.family.orEmpty()) }
    var plantType by rememberSaveable(plant?.id) { mutableStateOf(plant?.plantType.orEmpty()) }
    var origin by rememberSaveable(plant?.id) { mutableStateOf(plant?.origin.orEmpty()) }
    var sunlight by rememberSaveable(plant?.id) { mutableStateOf(plant?.sunlight.orEmpty()) }
    var attracts by rememberSaveable(plant?.id) { mutableStateOf(plant?.attracts.orEmpty()) }
    var fruitingSeason by rememberSaveable(plant?.id) { mutableStateOf(plant?.fruitingSeason.orEmpty()) }
    var harvestSeason by rememberSaveable(plant?.id) { mutableStateOf(plant?.harvestSeason.orEmpty()) }
    var harvestMethod by rememberSaveable(plant?.id) { mutableStateOf(plant?.harvestMethod.orEmpty()) }
    var description by rememberSaveable(plant?.id) { mutableStateOf(plant?.description.orEmpty()) }

    var isMedicinal by rememberSaveable(plant?.id) { mutableStateOf(plant?.isMedicinal) }
    var isPoisonousToHumans by rememberSaveable(plant?.id) { mutableStateOf(plant?.isPoisonousToHumans) }
    var isPoisonousToPets by rememberSaveable(plant?.id) { mutableStateOf(plant?.isPoisonousToPets) }

    LaunchedEffect(plant?.id) {
        speciesName = plant?.speciesName.orEmpty()
        scientificName = plant?.scientificName.orEmpty()
        family = plant?.family.orEmpty()
        plantType = plant?.plantType.orEmpty()
        origin = plant?.origin.orEmpty()
        sunlight = plant?.sunlight.orEmpty()
        attracts = plant?.attracts.orEmpty()
        fruitingSeason = plant?.fruitingSeason.orEmpty()
        harvestSeason = plant?.harvestSeason.orEmpty()
        harvestMethod = plant?.harvestMethod.orEmpty()
        description = plant?.description.orEmpty()
        isMedicinal = plant?.isMedicinal
        isPoisonousToHumans = plant?.isPoisonousToHumans
        isPoisonousToPets = plant?.isPoisonousToPets
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_edit_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_cd_back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = plant != null,
                        onClick = {
                            if (plant != null) {
                                onSaveClick(
                                    plant.copy(
                                        speciesName = speciesName.trim().ifBlank { plant.customName },
                                        scientificName = scientificName.trim().ifBlank { null },
                                        family = family.trim().ifBlank { null },
                                        plantType = plantType.trim().ifBlank { null },
                                        origin = origin.trim().ifBlank { null },
                                        sunlight = sunlight.trim().ifBlank { null },
                                        attracts = attracts.trim().ifBlank { null },
                                        fruitingSeason = fruitingSeason.trim().ifBlank { null },
                                        harvestSeason = harvestSeason.trim().ifBlank { null },
                                        harvestMethod = harvestMethod.trim().ifBlank { null },
                                        description = description.trim().ifBlank { null },
                                        isMedicinal = isMedicinal,
                                        isPoisonousToHumans = isPoisonousToHumans,
                                        isPoisonousToPets = isPoisonousToPets
                                    )
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.details_edit_cd_save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (plant == null) {
            Text(
                text = stringResource(R.string.garden_details_not_found),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = plant.customName,
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = speciesName,
                onValueChange = { speciesName = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_species)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            speciesName.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = scientificName,
                onValueChange = { scientificName = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_scientific_name)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            scientificName.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = family,
                onValueChange = { family = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_family)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            family.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = plantType,
                onValueChange = { plantType = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_type)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            plantType.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = origin,
                onValueChange = { origin = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_origin)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            origin.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = sunlight,
                onValueChange = { sunlight = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_sunlight)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            sunlight.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = attracts,
                onValueChange = { attracts = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_attracts)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            attracts.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = fruitingSeason,
                onValueChange = { fruitingSeason = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_fruiting_season)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            fruitingSeason.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = harvestSeason,
                onValueChange = { harvestSeason = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_harvest_season)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            harvestSeason.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = harvestMethod,
                onValueChange = { harvestMethod = normalizeShortFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_harvest_method)) },
                singleLine = true,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            harvestMethod.length,
                            SHORT_FIELD_MAX_LENGTH
                        )
                    )
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = normalizeLongFieldInput(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.details_edit_label_description)) },
                minLines = 5,
                maxLines = 10,
                supportingText = {
                    Text(
                        stringResource(
                            R.string.details_edit_counter_format,
                            description.length,
                            DESCRIPTION_MAX_LENGTH
                        )
                    )
                }
            )

            NullableBooleanSelector(
                title = stringResource(R.string.details_edit_label_medicinal),
                value = isMedicinal,
                onValueChange = { isMedicinal = it }
            )

            NullableBooleanSelector(
                title = stringResource(R.string.details_edit_label_poisonous_humans),
                value = isPoisonousToHumans,
                onValueChange = { isPoisonousToHumans = it }
            )

            NullableBooleanSelector(
                title = stringResource(R.string.details_edit_label_poisonous_pets),
                value = isPoisonousToPets,
                onValueChange = { isPoisonousToPets = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSaveClick(
                        plant.copy(
                            speciesName = speciesName.trim().ifBlank { plant.customName },
                            scientificName = scientificName.trim().ifBlank { null },
                            family = family.trim().ifBlank { null },
                            plantType = plantType.trim().ifBlank { null },
                            origin = origin.trim().ifBlank { null },
                            sunlight = sunlight.trim().ifBlank { null },
                            attracts = attracts.trim().ifBlank { null },
                            fruitingSeason = fruitingSeason.trim().ifBlank { null },
                            harvestSeason = harvestSeason.trim().ifBlank { null },
                            harvestMethod = harvestMethod.trim().ifBlank { null },
                            description = description.trim().ifBlank { null },
                            isMedicinal = isMedicinal,
                            isPoisonousToHumans = isPoisonousToHumans,
                            isPoisonousToPets = isPoisonousToPets
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}

@Composable
private fun NullableBooleanSelector(
    title: String,
    value: Boolean?,
    onValueChange: (Boolean?) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = value == null,
                onClick = { onValueChange(null) },
                label = { Text(stringResource(R.string.details_unknown)) }
            )

            FilterChip(
                selected = value == true,
                onClick = { onValueChange(true) },
                label = { Text(stringResource(R.string.common_yes)) }
            )

            FilterChip(
                selected = value == false,
                onClick = { onValueChange(false) },
                label = { Text(stringResource(R.string.common_no)) }
            )
        }
    }
}

private fun normalizeShortFieldInput(value: String): String {
    return value
        .replace("\n", " ")
        .replace("\r", " ")
        .take(SHORT_FIELD_MAX_LENGTH)
}

private fun normalizeLongFieldInput(value: String): String {
    return value.take(DESCRIPTION_MAX_LENGTH)
}
package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
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
    var pruningMonths by rememberSaveable(plant?.id) { mutableStateOf(plant?.pruningMonths.orEmpty()) }
    var pruningCountAmount by rememberSaveable(plant?.id) { mutableStateOf(plant?.pruningCountAmount?.toString().orEmpty()) }
    var pruningCountInterval by rememberSaveable(plant?.id) { mutableStateOf(plant?.pruningCountInterval.orEmpty()) }
    var growthRate by rememberSaveable(plant?.id) { mutableStateOf(plant?.growthRate.orEmpty()) }
    var soil by rememberSaveable(plant?.id) { mutableStateOf(plant?.soil.orEmpty()) }
    var fruitingSeason by rememberSaveable(plant?.id) { mutableStateOf(plant?.fruitingSeason.orEmpty()) }
    var harvestSeason by rememberSaveable(plant?.id) { mutableStateOf(plant?.harvestSeason.orEmpty()) }
    var harvestMethod by rememberSaveable(plant?.id) { mutableStateOf(plant?.harvestMethod.orEmpty()) }
    var description by rememberSaveable(plant?.id) { mutableStateOf(plant?.description.orEmpty()) }
    var careGuideWatering by rememberSaveable(plant?.id) { mutableStateOf(plant?.careGuideWatering.orEmpty()) }
    var careGuideSunlight by rememberSaveable(plant?.id) { mutableStateOf(plant?.careGuideSunlight.orEmpty()) }
    var careGuidePruning by rememberSaveable(plant?.id) { mutableStateOf(plant?.careGuidePruning.orEmpty()) }

    var isMedicinal by rememberSaveable(plant?.id) { mutableStateOf(plant?.isMedicinal) }
    var isPoisonousToHumans by rememberSaveable(plant?.id) { mutableStateOf(plant?.isPoisonousToHumans) }
    var isPoisonousToPets by rememberSaveable(plant?.id) { mutableStateOf(plant?.isPoisonousToPets) }
    var isRare by rememberSaveable(plant?.id) { mutableStateOf(plant?.rare) }

    LaunchedEffect(plant?.id) {
        speciesName = plant?.speciesName.orEmpty()
        scientificName = plant?.scientificName.orEmpty()
        family = plant?.family.orEmpty()
        plantType = plant?.plantType.orEmpty()
        origin = plant?.origin.orEmpty()
        sunlight = plant?.sunlight.orEmpty()
        attracts = plant?.attracts.orEmpty()
        pruningMonths = plant?.pruningMonths.orEmpty()
        pruningCountAmount = plant?.pruningCountAmount?.toString().orEmpty()
        pruningCountInterval = plant?.pruningCountInterval.orEmpty()
        growthRate = plant?.growthRate.orEmpty()
        soil = plant?.soil.orEmpty()
        fruitingSeason = plant?.fruitingSeason.orEmpty()
        harvestSeason = plant?.harvestSeason.orEmpty()
        harvestMethod = plant?.harvestMethod.orEmpty()
        description = plant?.description.orEmpty()
        careGuideWatering = plant?.careGuideWatering.orEmpty()
        careGuideSunlight = plant?.careGuideSunlight.orEmpty()
        careGuidePruning = plant?.careGuidePruning.orEmpty()
        isMedicinal = plant?.isMedicinal
        isPoisonousToHumans = plant?.isPoisonousToHumans
        isPoisonousToPets = plant?.isPoisonousToPets
        isRare = plant?.rare
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details_edit_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
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
                                        speciesName = speciesName.trim().ifBlank { null },
                                        scientificName = scientificName.trim().ifBlank { null },
                                        family = family.trim().ifBlank { null },
                                        plantType = plantType.trim().ifBlank { null },
                                        origin = origin.trim().ifBlank { null },
                                        sunlight = sunlight.trim().ifBlank { null },
                                        attracts = attracts.trim().ifBlank { null },
                                        pruningMonths = pruningMonths.trim().ifBlank { null },
                                        pruningCountAmount = pruningCountAmount.trim().toIntOrNull(),
                                        pruningCountInterval = pruningCountInterval.trim().ifBlank { null },
                                        growthRate = growthRate.trim().ifBlank { null },
                                        soil = soil.trim().ifBlank { null },
                                        rare = isRare,
                                        careGuideWatering = careGuideWatering.trim().ifBlank { null },
                                        careGuideSunlight = careGuideSunlight.trim().ifBlank { null },
                                        careGuidePruning = careGuidePruning.trim().ifBlank { null },
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
                            painter = painterResource(R.drawable.ic_check),
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

            EditFormSection(title = stringResource(R.string.details_edit_section_basic)) {
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
            }

            EditFormSection(title = stringResource(R.string.details_edit_section_care)) {
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
                    value = pruningMonths,
                    onValueChange = { pruningMonths = normalizeShortFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_pruning_months)) },
                    singleLine = true,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                pruningMonths.length,
                                SHORT_FIELD_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = pruningCountAmount,
                    onValueChange = { pruningCountAmount = normalizeShortFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_pruning_count_amount)) },
                    singleLine = true,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                pruningCountAmount.length,
                                SHORT_FIELD_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = pruningCountInterval,
                    onValueChange = { pruningCountInterval = normalizeShortFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_pruning_count_interval)) },
                    singleLine = true,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                pruningCountInterval.length,
                                SHORT_FIELD_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = growthRate,
                    onValueChange = { growthRate = normalizeShortFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_growth_rate)) },
                    singleLine = true,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                growthRate.length,
                                SHORT_FIELD_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = soil,
                    onValueChange = { soil = normalizeShortFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_soil)) },
                    singleLine = true,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                soil.length,
                                SHORT_FIELD_MAX_LENGTH
                            )
                        )
                    }
                )

                NullableBooleanSelector(
                    title = stringResource(R.string.details_edit_label_rare),
                    value = isRare,
                    onValueChange = { isRare = it }
                )
            }

            EditFormSection(title = stringResource(R.string.details_edit_section_care_guide)) {
                OutlinedTextField(
                    value = careGuideWatering,
                    onValueChange = { careGuideWatering = normalizeLongFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_care_watering)) },
                    minLines = 4,
                    maxLines = 8,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                careGuideWatering.length,
                                DESCRIPTION_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = careGuideSunlight,
                    onValueChange = { careGuideSunlight = normalizeLongFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_care_sunlight)) },
                    minLines = 4,
                    maxLines = 8,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                careGuideSunlight.length,
                                DESCRIPTION_MAX_LENGTH
                            )
                        )
                    }
                )

                OutlinedTextField(
                    value = careGuidePruning,
                    onValueChange = { careGuidePruning = normalizeLongFieldInput(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.details_edit_label_care_pruning)) },
                    minLines = 4,
                    maxLines = 8,
                    supportingText = {
                        Text(
                            stringResource(
                                R.string.details_edit_counter_format,
                                careGuidePruning.length,
                                DESCRIPTION_MAX_LENGTH
                            )
                        )
                    }
                )
            }

            EditFormSection(title = stringResource(R.string.details_edit_section_description)) {
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
            }

            EditFormSection(title = stringResource(R.string.details_edit_section_safety)) {
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
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onSaveClick(
                        plant.copy(
                            speciesName = speciesName.trim().ifBlank { null },
                            scientificName = scientificName.trim().ifBlank { null },
                            family = family.trim().ifBlank { null },
                            plantType = plantType.trim().ifBlank { null },
                            origin = origin.trim().ifBlank { null },
                            sunlight = sunlight.trim().ifBlank { null },
                            attracts = attracts.trim().ifBlank { null },
                            pruningMonths = pruningMonths.trim().ifBlank { null },
                            pruningCountAmount = pruningCountAmount.trim().toIntOrNull(),
                            pruningCountInterval = pruningCountInterval.trim().ifBlank { null },
                            growthRate = growthRate.trim().ifBlank { null },
                            soil = soil.trim().ifBlank { null },
                            rare = isRare,
                            careGuideWatering = careGuideWatering.trim().ifBlank { null },
                            careGuideSunlight = careGuideSunlight.trim().ifBlank { null },
                            careGuidePruning = careGuidePruning.trim().ifBlank { null },
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

@Composable
private fun EditFormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            content()
        }
    }
}

package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smart_plant_care.R
import kotlin.math.roundToInt

const val MANUAL_ENTRY_SPECIES_TOKEN = "__manual_entry__"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    speciesName: String,
    defaultWaterDays: Int,
    initialCustomName: String = "",
    isEditing: Boolean = false,
    onSaveClick: (String, Int) -> Unit,
    onBackClick: () -> Unit
) {
    val isManual = speciesName == MANUAL_ENTRY_SPECIES_TOKEN
    val defaultManualName = stringResource(R.string.edit_default_manual_name)
    var customName by remember(initialCustomName) { mutableStateOf(initialCustomName) }
    var savedWaterDays by remember(defaultWaterDays) {
        mutableIntStateOf(defaultWaterDays.coerceIn(1, 30))
    }
    var draftWaterDays by remember(defaultWaterDays) {
        mutableFloatStateOf(defaultWaterDays.coerceIn(1, 30).toFloat())
    }
    var isWaterEditorExpanded by remember(defaultWaterDays, isManual, isEditing) {
        mutableStateOf(isManual && !isEditing)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isEditing -> stringResource(R.string.edit_title_edit)
                            isManual -> stringResource(R.string.edit_title_create)
                            else -> stringResource(R.string.edit_title_settings)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.edit_cd_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val finalName = when {
                        customName.isNotBlank() -> customName
                        isEditing && initialCustomName.isNotBlank() -> initialCustomName
                        isManual -> defaultManualName
                        else -> speciesName
                    }
                    val finalWaterDays = if (isWaterEditorExpanded) {
                        draftWaterDays.roundToInt().coerceIn(1, 30)
                    } else {
                        savedWaterDays
                    }
                    onSaveClick(finalName, finalWaterDays)
                },
                icon = { Icon(Icons.Default.Check, contentDescription = stringResource(R.string.edit_cd_save)) },
                text = {
                    Text(
                        if (isEditing) {
                            stringResource(R.string.edit_save_changes)
                        } else {
                            stringResource(R.string.edit_save_to_garden)
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!isManual) {
                Text(
                    text = stringResource(R.string.edit_selected_format, speciesName),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = {
                    Text(
                        when {
                            isEditing -> stringResource(R.string.edit_label_plant_name)
                            isManual -> stringResource(R.string.edit_label_plant_name_required)
                            else -> stringResource(R.string.edit_label_custom_name_optional)
                        }
                    )
                },
                placeholder = { Text(stringResource(R.string.edit_placeholder_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                if (isWaterEditorExpanded) {
                    val previewDays = draftWaterDays.roundToInt().coerceIn(1, 30)
                    Text(
                        text = pluralStringResource(
                            R.plurals.edit_water_every_days,
                            previewDays,
                            previewDays
                        ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Slider(
                        value = draftWaterDays,
                        onValueChange = { draftWaterDays = it },
                        valueRange = 1f..30f,
                        steps = 28
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                savedWaterDays = previewDays
                                draftWaterDays = previewDays.toFloat()
                                isWaterEditorExpanded = false
                            }
                        ) {
                            Text(stringResource(R.string.edit_save_interval))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.edit_water_every_days,
                                savedWaterDays,
                                savedWaterDays
                            ),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        OutlinedButton(
                            onClick = {
                                draftWaterDays = savedWaterDays.toFloat()
                                isWaterEditorExpanded = true
                            }
                        ) {
                            Text(stringResource(R.string.edit_change_interval))
                        }
                    }
                }
            }
        }
    }
}

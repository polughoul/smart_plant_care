package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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


private const val NOTE_MAX_LENGTH = 2000
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantNotesScreen(
    plant: MyPlantEntity?,
    onBackClick: () -> Unit,
    onSaveClick: (String?) -> Unit
) {
    var noteText by rememberSaveable(plant?.id) {
        mutableStateOf(plant?.noteText.orEmpty())
    }

    LaunchedEffect(plant?.id, plant?.noteText) {
        noteText = plant?.noteText.orEmpty()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.notes_screen_title))
                },
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
                        onClick = {
                            onSaveClick(noteText.trim().ifBlank { null })
                        },
                        enabled = plant != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(R.string.notes_screen_save_cd)
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
                .padding(16.dp)
        ) {
            Text(
                text = plant.customName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = normalizeNoteInput(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        label = { Text(stringResource(R.string.notes_screen_label)) },
                        placeholder = { Text(stringResource(R.string.notes_screen_placeholder)) },
                        minLines = 10
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = stringResource(
                                R.string.notes_screen_counter_format,
                                noteText.length,
                                NOTE_MAX_LENGTH
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onSaveClick(noteText.trim().ifBlank { null })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.common_save))
            }
        }
    }
}

private fun normalizeNoteInput(value: String): String {
    return value.take(NOTE_MAX_LENGTH)
}
package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
    val isManual = speciesName == "Manual Entry"
    var customName by remember(initialCustomName) { mutableStateOf(initialCustomName) }
    var waterDays by remember(defaultWaterDays) { mutableStateOf(defaultWaterDays.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            isEditing -> "Edit Plant"
                            isManual -> "Create New Plant"
                            else -> "Plant Settings"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val finalName = when {
                        customName.isNotBlank() -> customName
                        isEditing && initialCustomName.isNotBlank() -> initialCustomName
                        isManual -> "My Plant"
                        else -> speciesName
                    }
                    onSaveClick(finalName, waterDays.roundToInt())
                },
                icon = { Icon(Icons.Default.Check, "Save") },
                text = { Text(if (isEditing) "Save changes" else "Save to Garden") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (!isManual) {
                Text(
                    text = "Selected: $speciesName",
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
                            isEditing -> "Plant Name"
                            isManual -> "Plant Name (Required)"
                            else -> "Custom Name (Optional)"
                        }
                    )
                },
                placeholder = { Text("e.g. My Kitchen Ficus") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column {
                Text(
                    text = "Water every ${waterDays.roundToInt()} days",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = waterDays,
                    onValueChange = { waterDays = it },
                    valueRange = 1f..30f,
                    steps = 28
                )
            }
        }
    }
}
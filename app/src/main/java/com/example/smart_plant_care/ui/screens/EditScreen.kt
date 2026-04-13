package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
    onSaveClick: (String, Int) -> Unit,
    onBackClick: () -> Unit
) {
    var customName by remember { mutableStateOf("") }
    var waterDays by remember { mutableStateOf(3f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plant Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    val finalName = customName.ifBlank { speciesName }
                    onSaveClick(finalName, waterDays.roundToInt())
                },
                icon = { Icon(Icons.Default.Check, contentDescription = "Save") },
                text = { Text("Save to Garden") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Selected: $speciesName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text("Give it a name?") },
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
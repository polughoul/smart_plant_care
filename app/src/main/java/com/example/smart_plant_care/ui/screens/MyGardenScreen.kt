package com.example.smart_plant_care.ui.screens

import android.icu.text.StringSearch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Delete
import com.example.smart_plant_care.ui.viewmodels.GardenViewModel


@Composable
fun PlantCard(name: String, status: String, onDeleteClick: () -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            Spacer(modifier = Modifier.width(16.dp))

            Column{
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = status, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGardenScreen(viewModel: GardenViewModel, onNavigateToSearch: () -> Unit) {

    val plants by viewModel.plantsList.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Garden")},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSearch) {
                Icon(Icons.Default.Add, contentDescription = "find plant")
            }
        }
    ) { paddingValues ->

        if (plants.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No plants yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(plants) { plant ->
                    PlantCard(
                        name = plant.customName,
                        status = calculateDaysRemaining(plant.nextWateringDate),
                        onDeleteClick = {  viewModel.deletePlant(plant.id) }
                    )
                }
            }
        }
    }
}

fun calculateDaysRemaining(nextWateringMillis: Long): String {
    val diff = nextWateringMillis - System.currentTimeMillis()
    val days = (diff / (24 * 60 * 60 * 1000)).toInt()
    return when {
        days < 0 -> "Need to water immediatly"
        days == 0 -> "Water today"
        days == 1 -> "Water tomorrow"
        else -> "Water after $days days"
    }
}
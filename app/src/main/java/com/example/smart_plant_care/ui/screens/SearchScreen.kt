package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import com.example.smart_plant_care.ui.viewmodels.SearchUiState
import com.example.smart_plant_care.ui.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onPlantClick: (Int) -> Unit,
    searchViewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by searchViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find Plant") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ficus") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { searchViewModel.searchPlants(searchQuery) }),
                trailingIcon = {
                    IconButton(onClick = { searchViewModel.searchPlants(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onPlantClick(-1) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Create new plant")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                SearchUiState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Enter a plant name to start search")
                    }
                }

                SearchUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is SearchUiState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.plants) { plant ->
                            ApiPlantCard(plant = plant, onClick = { onPlantClick(plant.id) })
                        }
                    }
                }

                SearchUiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No plants found for this query")
                    }
                }

                is SearchUiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(onClick = searchViewModel::retryLastSearch) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiPlantCard(plant: ApiPlantDto, onClick: () -> Unit) {
    val scientificText = plant.scientificName?.firstOrNull()?.takeIf { it.isNotBlank() } ?: "Unknown"
    val wateringText = plant.watering?.takeIf { it.isNotBlank() }
    val sunlightText = plant.sunlight?.joinToString()?.takeIf { it.isNotBlank() }

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = plant.defaultImage?.thumbnail ?: "https://via.placeholder.com/150",
                contentDescription = plant.commonName,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = plant.commonName, style = MaterialTheme.typography.titleMedium)
                Text(text = scientificText, style = MaterialTheme.typography.bodySmall)
                if (wateringText != null) {
                    Text(text = "Watering: $wateringText", style = MaterialTheme.typography.bodySmall)
                }
                if (sunlightText != null) {
                    Text(text = "Sunlight: $sunlightText", style = MaterialTheme.typography.bodySmall)
                }
                if (wateringText == null && sunlightText == null) {
                    Text(
                        text = "No watering/sunlight data in search response",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

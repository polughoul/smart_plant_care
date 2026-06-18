package com.example.smart_plant_care.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smart_plant_care.R
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.ui.util.rememberDayToken
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.setValue



private enum class WateringHistoryFilter {
    ALL,
    THIS_MONTH,
    THIS_YEAR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringHistoryScreen(
    plant: MyPlantEntity?,
    wateringEvents: List<WateringEventEntity>,
    onBackClick: () -> Unit,
    onClearHistory: () -> Unit
) {
    var selectedFilter by remember {
        mutableStateOf(WateringHistoryFilter.ALL)
    }
    var showClearDialog by remember { mutableStateOf(false) }
    val dayToken = rememberDayToken()

    val filteredEvents = remember(wateringEvents, selectedFilter, dayToken) {
        val today = dayToken

        wateringEvents.filter { event ->
            val eventDate = Instant
                .ofEpochMilli(event.wateredAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            when (selectedFilter) {
                WateringHistoryFilter.ALL -> true

                WateringHistoryFilter.THIS_MONTH -> {
                    eventDate.year == today.year &&
                            eventDate.month == today.month
                }

                WateringHistoryFilter.THIS_YEAR -> {
                    eventDate.year == today.year
                }
            }
        }
    }

    val datePattern = stringResource(R.string.watering_history_date_format)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.garden_details_history_title))
                },
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
                        onClick = { showClearDialog = true },
                        enabled = wateringEvents.isNotEmpty()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = stringResource(R.string.watering_history_clear)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text(stringResource(R.string.watering_history_clear_title)) },
                text = { Text(stringResource(R.string.watering_history_clear_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showClearDialog = false
                            onClearHistory()
                        }
                    ) {
                        Text(stringResource(R.string.watering_history_clear_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) {
                        Text(stringResource(R.string.watering_history_clear_cancel))
                    }
                }
            )
        }
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = plant.customName,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == WateringHistoryFilter.ALL,
                        onClick = {
                            selectedFilter = WateringHistoryFilter.ALL
                        },
                        label = {
                            Text(stringResource(R.string.watering_history_filter_all))
                        }
                    )

                    FilterChip(
                        selected = selectedFilter == WateringHistoryFilter.THIS_MONTH,
                        onClick = {
                            selectedFilter = WateringHistoryFilter.THIS_MONTH
                        },
                        label = {
                            Text(stringResource(R.string.watering_history_filter_month))
                        }
                    )

                    FilterChip(
                        selected = selectedFilter == WateringHistoryFilter.THIS_YEAR,
                        onClick = {
                            selectedFilter = WateringHistoryFilter.THIS_YEAR
                        },
                        label = {
                            Text(stringResource(R.string.watering_history_filter_year))
                        }
                    )
                }
            }

            if (wateringEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.garden_details_history_empty),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else if (filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.watering_history_empty_filter),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = filteredEvents,
                        key = { _, event -> event.id }
                    ) { index, event ->
                        val isLast = index == filteredEvents.lastIndex
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(10.dp)
                                        .width(10.dp)
                                        .padding(top = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.extraSmall
                                            )
                                    )
                                }

                                if (!isLast) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .width(2.dp)
                                            .height(24.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = formatWateringHistoryDate(event.wateredAt, datePattern),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatWateringHistoryDate(epochMillis: Long, pattern: String): String {
    val dateTime = Instant
        .ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())

    val formatter = DateTimeFormatter.ofPattern(
        pattern,
        Locale.getDefault()
    )

    return formatter.format(dateTime)
}

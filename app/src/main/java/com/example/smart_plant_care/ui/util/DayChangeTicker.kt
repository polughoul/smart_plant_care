package com.example.smart_plant_care.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.max
import kotlinx.coroutines.delay

@Composable
fun rememberDayToken(): LocalDate {
    var dayToken by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(dayToken) {
        val zone = ZoneId.systemDefault()
        val now = System.currentTimeMillis()
        val nextMidnight = ZonedDateTime.now(zone)
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
        val delayMillis = max(1L, nextMidnight - now)
        delay(delayMillis)
        dayToken = LocalDate.now()
    }

    return dayToken
}


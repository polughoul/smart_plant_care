package com.example.smart_plant_care.util

fun calculateNextWateringDate(
    waterIntervalDays: Int,
    nowMillis: Long = System.currentTimeMillis()
): Long {
    val days = waterIntervalDays.coerceAtLeast(1)
    return nowMillis + days * 24L * 60L * 60L * 1000L
}


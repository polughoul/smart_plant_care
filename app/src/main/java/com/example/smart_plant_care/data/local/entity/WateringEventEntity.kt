package com.example.smart_plant_care.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watering_events",
    indices = [Index(value = ["plantId"])]
)
data class WateringEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plantId: Int,
    val wateredAt: Long
)


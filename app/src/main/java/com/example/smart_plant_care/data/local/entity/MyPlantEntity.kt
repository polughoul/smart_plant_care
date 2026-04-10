package com.example.smart_plant_care.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "my_plants")
data class MyPlantEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val customName: String,
    val speciesName: String,
    val waterIntervalDays: Int,
    val nextWateringDate: Long
)
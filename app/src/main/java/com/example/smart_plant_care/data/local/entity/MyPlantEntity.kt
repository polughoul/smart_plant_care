package com.example.smart_plant_care.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "my_plants",
    indices = [Index(value = ["remotePlantId"], unique = true)]
)
data class MyPlantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customName: String,
    val speciesName: String,
    val remotePlantId: Int? = null,
    val scientificName: String? = null,
    val family: String? = null,
    val origin: String? = null,
    val plantType: String? = null,
    val sunlight: String? = null,
    val attracts: String? = null,
    val description: String? = null,
    val waterIntervalDays: Int,
    val nextWateringDate: Long,
    val fruitingSeason: String? = null,
    val harvestSeason: String? = null,
    val harvestMethod: String? = null,
    val isMedicinal: Boolean = false,
    val isPoisonousToHumans: Boolean = false,
    val isPoisonousToPets: Boolean = false,
    val noteText: String? = null,
    val imageUrl: String? = null
)

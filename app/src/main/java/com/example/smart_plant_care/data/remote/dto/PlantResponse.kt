package com.example.smart_plant_care.data.remote.dto

import com.google.gson.annotations.SerializedName


data class ApiPlantDto(
    val id: Int,
    @SerializedName("common_name") val commonName: String,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("default_image") val defaultImage: PlantImage?
)

data class PlantImage(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("regular_url") val regularUrl: String?
)


data class PlantListResponce(
    @SerializedName("data") val data: List<ApiPlantDto>
)

data class PlantDetailsDto(
    val id: Int,
    @SerializedName("common_name") val commonName: String,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    val family: String?,
    val origin: String?,
    val type: String?,
    val watering: String?,
    @SerializedName("watering_general_benchmark") val wateringBenchmark: WateringBenchmark?,
    val sunlight: List<String>?,
    val attracts: String?,
    val description: String?,
    @SerializedName("fruiting_season") val fruitingSeason: String?,
    @SerializedName("harvest_season") val harvestSeason: String?,
    @SerializedName("harvest_method") val harvestMethod: String?,
    @SerializedName("medicinal") val Medicinal: Boolean?,
    @SerializedName("poisonous_to_humans") val poisonousToHumans: Int?,
    @SerializedName("poisonous_to_pets") val poisonousToPets: Int?,
    @SerializedName("default_image") val defaultImage: PlantImage?
)

data class WateringBenchmark(
    val value: String?,
    val unit: String?
)
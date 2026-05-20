package com.example.smart_plant_care.data.remote.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName


data class PlantListResponce(
    @SerializedName("data") val data: List<ApiPlantDto>
)

data class CareGuideListResponse(
    @SerializedName("data") val data: List<CareGuideDto>
)

data class CareGuideDto(
    @SerializedName("section") val section: List<CareSectionDto>?
)

data class CareSectionDto(
    @SerializedName("type") val type: String?,
    @SerializedName("description") val description: String?
)

data class ApiPlantDto(
    @SerializedName("id") val id: Int,
    @SerializedName("common_name") val commonName: String,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("watering") val watering: String?,
    @SerializedName("sunlight") val sunlight: List<String>?,
    @SerializedName("default_image") val defaultImage: PlantImage?
)

data class PlantImage(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("small_url") val smallUrl: String?,
    @SerializedName("medium_url") val mediumUrl: String?,
    @SerializedName("regular_url") val regularUrl: String?,
    @SerializedName("original_url") val originalUrl: String?
)

fun PlantImage?.bestImageUrl(): String? {
    return this?.regularUrl
        ?: this?.mediumUrl
        ?: this?.smallUrl
        ?: this?.thumbnail
        ?: this?.originalUrl
}

data class PlantDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("common_name") val commonName: String,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("family") val family: String?,
    @SerializedName("origin") val origin: List<String>?,
    @SerializedName("type") val type: String?,
    @SerializedName("watering") val watering: String?,
    @SerializedName("watering_general_benchmark") val wateringBenchmark: WateringBenchmark?,
    @SerializedName("sunlight") val sunlight: List<String>?,
    @SerializedName("attracts") val attracts: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("fruiting_season") val fruitingSeason: String?,
    @SerializedName("harvest_season") val harvestSeason: String?,
    @SerializedName("harvest_method") val harvestMethod: String?,
    @SerializedName("medicinal") val medicinal: Boolean?,
    @SerializedName("poisonous_to_humans") val poisonousToHumans: Boolean?,
    @SerializedName("poisonous_to_pets") val poisonousToPets: Boolean?,
    @SerializedName("pruning_month") val pruningMonths: List<String>? = null,
    @JsonAdapter(PruningCountAdapter::class)
    @SerializedName("pruning_count") val pruningCount: PruningCount? = null,
    @SerializedName("growth_rate") val growthRate: String? = null,
    @SerializedName("soil") val soil: List<String>? = null,
    @SerializedName("rare") val rare: Boolean? = null,
    @SerializedName("default_image") val defaultImage: PlantImage?
)

data class PruningCount(
    @SerializedName("amount") val amount: Int?,
    @SerializedName("interval") val interval: String?
)

data class WateringBenchmark(
    @SerializedName("value") val value: String?,
    @SerializedName("unit") val unit: String?
)
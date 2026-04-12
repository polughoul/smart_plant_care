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
    val watering: String?,
    val sunlight: List<String>?,
    val description: String?,
    @SerializedName("default_image") val defaultImage: PlantImage?
)
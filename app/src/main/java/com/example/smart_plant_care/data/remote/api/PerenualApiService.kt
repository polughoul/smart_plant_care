package com.example.smart_plant_care.data.remote.api

import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.PlantListResponce
import com.example.smart_plant_care.data.remote.dto.CareGuideListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PerenualApiService {

    @GET("api/v2/species-list")
    suspend fun searchPlants(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): PlantListResponce

    @GET("api/v2/species/details/{id}")
    suspend fun getPlantDetails(
        @retrofit2.http.Path("id") id: Int,
        @Query("key") apiKey: String
    ): PlantDetailsDto

    @GET("api/species-care-guide-list")
    suspend fun getSpeciesCareGuide(
        @Query("species_id") speciesId: Int,
        @Query("key") apiKey: String
    ): CareGuideListResponse
}
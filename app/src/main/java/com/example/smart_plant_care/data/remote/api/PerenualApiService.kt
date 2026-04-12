package com.example.smart_plant_care.data.remote.api

import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.PlantListResponce
import retrofit2.http.GET
import retrofit2.http.Query

interface PerenualApiService {

    @GET("api/species-list")
    suspend fun searchPlants(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): PlantListResponce

    @GET("api/species/details/{id}")
    suspend fun getPlantDetails(
        @retrofit2.http.Path("id") id: Int,
        @Query("key") apiKey: String
    ): PlantDetailsDto
}
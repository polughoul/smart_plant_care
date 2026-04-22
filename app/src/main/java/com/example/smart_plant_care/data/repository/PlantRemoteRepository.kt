package com.example.smart_plant_care.data.repository

import com.example.smart_plant_care.data.remote.api.PerenualApiService
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import com.example.smart_plant_care.data.remote.dto.CareSectionDto
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class Error(val message: String) : RemoteResult<Nothing>
}

data class PlantDetailsPayload(
    val plant: PlantDetailsDto,
    val careSections: List<CareSectionDto>
)

class PlantRemoteRepository(
    private val apiService: PerenualApiService = RetrofitClient.apiService
) {

    suspend fun searchPlants(query: String, apiKey: String): RemoteResult<List<ApiPlantDto>> {
        return runCatching {
            val plants = apiService.searchPlants(query = query, apiKey = apiKey).data
            RemoteResult.Success(plants)
        }.getOrElse { exception ->
            RemoteResult.Error(mapRemoteError(exception))
        }
    }

    suspend fun getPlantDetails(plantId: Int, apiKey: String): RemoteResult<PlantDetailsPayload> {
        return runCatching {
            val details = apiService.getPlantDetails(plantId, apiKey)
            val shouldLoadCareGuide = details.watering.isNullOrBlank() || details.sunlight.isNullOrEmpty()
            val careSections = if (shouldLoadCareGuide) {
                runCatching {
                    apiService
                        .getSpeciesCareGuide(plantId, apiKey)
                        .data
                        .firstOrNull()
                        ?.section
                        .orEmpty()
                }.getOrDefault(emptyList())
            } else {
                emptyList()
            }
            RemoteResult.Success(
                PlantDetailsPayload(
                    plant = details,
                    careSections = careSections
                )
            )
        }.getOrElse { exception ->
            RemoteResult.Error(mapRemoteError(exception))
        }
    }

    private fun mapRemoteError(exception: Throwable): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                401, 403 -> "API key is invalid or unauthorized. Check PERENUAL_API_KEY in local.properties."
                404 -> "Plant API endpoint not found (HTTP 404)."
                429 -> "API rate limit reached. Wait a minute and retry."
                in 500..599 -> "Plant API temporary server error (${exception.code()}). Try again later."
                else -> "Plant API request failed (HTTP ${exception.code()})."
            }

            is SocketTimeoutException -> "Request timeout. Check internet connection and retry."
            is IOException -> "No internet connection or network error."
            else -> "Failed to load data from plant API. Try again."
        }
    }
}

package com.example.smart_plant_care.data.repository

import com.example.smart_plant_care.data.remote.api.PerenualApiService
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import com.example.smart_plant_care.data.remote.dto.CareGuideListResponse
import com.example.smart_plant_care.data.remote.dto.CareGuideDto
import com.example.smart_plant_care.data.remote.dto.CareSectionDto
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.PlantImage
import com.example.smart_plant_care.data.remote.dto.PlantListResponce
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantRemoteRepositoryTest {

    @Test
    fun searchPlants_returnsSuccessList() = runBlocking {
        val service = FakePerenualApiService(
            onSearch = { _, _ ->
                PlantListResponce(
                    data = listOf(
                        ApiPlantDto(
                            id = 42,
                            commonName = "Ficus",
                            scientificName = listOf("Ficus benjamina"),
                            watering = "Average",
                            sunlight = listOf("bright indirect"),
                            defaultImage = PlantImage(
                                thumbnail = "thumb",
                                smallUrl = null,
                                mediumUrl = null,
                                regularUrl = "regular",
                                originalUrl = null
                            )
                        )
                    )
                )
            }
        )
        val repository = PlantRemoteRepository(service)

        val result = repository.searchPlants("ficus", "api-key")

        assertTrue(result is RemoteResult.Success)
        val plants = (result as RemoteResult.Success).data
        assertEquals(1, plants.size)
        assertEquals("Ficus", plants.first().commonName)
    }

    @Test
    fun getPlantDetails_loadsCareGuideWhenWateringAndSunlightAreMissing() = runBlocking {
        var careGuideRequested = false
        val service = FakePerenualApiService(
            onDetails = { _, _ ->
                PlantDetailsDto(
                    id = 10,
                    commonName = "Monstera",
                    scientificName = listOf("Monstera deliciosa"),
                    family = null,
                    origin = null,
                    type = null,
                    watering = null,
                    wateringBenchmark = null,
                    sunlight = emptyList(),
                    attracts = null,
                    description = null,
                    fruitingSeason = null,
                    harvestSeason = null,
                    harvestMethod = null,
                    medicinal = null,
                    poisonousToHumans = null,
                    poisonousToPets = null,
                    defaultImage = null
                )
            },
            onCareGuide = { _, _ ->
                careGuideRequested = true
                CareGuideListResponse(
                    data = listOf(
                        CareGuideDto(
                            section = listOf(
                                CareSectionDto(
                                    type = "watering",
                                    description = "Water once a week."
                                )
                            )
                        )
                    )
                )
            }
        )
        val repository = PlantRemoteRepository(service)

        val result = repository.getPlantDetails(10, "api-key")

        assertTrue(result is RemoteResult.Success)
        val payload = (result as RemoteResult.Success).data
        assertEquals("Monstera", payload.plant.commonName)
        assertEquals(1, payload.careSections.size)
        assertTrue(careGuideRequested)
    }

    @Test
    fun searchPlants_returnsMappedErrorOnIoException() = runBlocking {
        val service = FakePerenualApiService(
            onSearch = { _, _ -> throw IOException("network down") }
        )
        val repository = PlantRemoteRepository(service)

        val result = repository.searchPlants("ficus", "api-key")

        assertTrue(result is RemoteResult.Error)
        assertEquals(
            "No internet connection or network error.",
            (result as RemoteResult.Error).message
        )
    }
}

private class FakePerenualApiService(
    private val onSearch: suspend (query: String, apiKey: String) -> PlantListResponce = { _, _ ->
        throw UnsupportedOperationException("search not used")
    },
    private val onDetails: suspend (id: Int, apiKey: String) -> PlantDetailsDto = { _, _ ->
        throw UnsupportedOperationException("details not used")
    },
    private val onCareGuide: suspend (speciesId: Int, apiKey: String) -> CareGuideListResponse = { _, _ ->
        throw UnsupportedOperationException("care guide not used")
    }
) : PerenualApiService {
    override suspend fun searchPlants(query: String, apiKey: String): PlantListResponce {
        return onSearch(query, apiKey)
    }

    override suspend fun getPlantDetails(id: Int, apiKey: String): PlantDetailsDto {
        return onDetails(id, apiKey)
    }

    override suspend fun getSpeciesCareGuide(speciesId: Int, apiKey: String): CareGuideListResponse {
        return onCareGuide(speciesId, apiKey)
    }
}

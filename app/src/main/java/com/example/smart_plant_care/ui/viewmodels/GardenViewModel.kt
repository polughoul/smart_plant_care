package com.example.smart_plant_care.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.data.remote.dto.CareSectionDto
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import com.example.smart_plant_care.data.remote.dto.bestImageUrl
import com.example.smart_plant_care.data.repository.InsertPlantResult
import com.example.smart_plant_care.data.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GardenViewModel(private val repository: PlantRepository) : ViewModel() {
    private val imageCacheAttempts = mutableSetOf<Int>()

    val plantsList: StateFlow<List<MyPlantEntity>> = repository.getAllPlants()
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun deletePlant(plantId: Int) {
        viewModelScope.launch {
            repository.deletePlant(plantId)
        }
    }

    fun insertPlant(plant: MyPlantEntity, onResult: ((InsertPlantResult) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.insertPlant(plant)
            onResult?.invoke(result)
        }
    }

    fun insertPlantWithImageCaching(
        context: Context,
        plant: MyPlantEntity,
        onResult: ((InsertPlantResult) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cachedPlant = repository.cacheRemoteImageForPlant(context, plant)
            val result = repository.insertPlant(cachedPlant)
            onResult?.invoke(result)
        }
    }

    fun updatePlant(plant: MyPlantEntity) {
        viewModelScope.launch {
            repository.updatePlant(plant)
        }
    }

    fun addPlantFromDetails(
        context: Context,
        detailsDto: PlantDetailsDto,
        defaultWaterDays: Int,
        careSections: List<CareSectionDto>,
        onResult: ((InsertPlantResult) -> Unit)? = null
    ) {
        val careGuideWatering = careSections
            .firstOrNull { it.type.equals("watering", ignoreCase = true) }
            ?.description
            ?.takeIf { it.isNotBlank() }
        val careGuideSunlight = careSections
            .firstOrNull { it.type.equals("sunlight", ignoreCase = true) }
            ?.description
            ?.takeIf { it.isNotBlank() }
        val careGuidePruning = careSections
            .firstOrNull { it.type.equals("pruning", ignoreCase = true) }
            ?.description
            ?.takeIf { it.isNotBlank() }

        val newPlant = MyPlantEntity(
            customName = detailsDto.commonName,
            speciesName = detailsDto.commonName,
            remotePlantId = detailsDto.id,
            scientificName = detailsDto.scientificName?.firstOrNull(),
            family = detailsDto.family,
            origin = detailsDto.origin?.joinToString(),
            plantType = detailsDto.type,
            sunlight = detailsDto.sunlight?.joinToString(),
            attracts = detailsDto.attracts?.joinToString(),
            pruningMonths = detailsDto.pruningMonths?.joinToString(),
            pruningCountAmount = detailsDto.pruningCount?.amount,
            pruningCountInterval = detailsDto.pruningCount?.interval,
            growthRate = detailsDto.growthRate,
            soil = detailsDto.soil?.joinToString(),
            rare = detailsDto.rare,
            careGuideWatering = careGuideWatering,
            careGuideSunlight = careGuideSunlight,
            careGuidePruning = careGuidePruning,
            description = detailsDto.description,
            waterIntervalDays = defaultWaterDays,
            nextWateringDate = calculateNextWateringDateMillis(defaultWaterDays),
            fruitingSeason = detailsDto.fruitingSeason,
            harvestSeason = detailsDto.harvestSeason,
            harvestMethod = detailsDto.harvestMethod,
            isMedicinal = detailsDto.medicinal,
            isPoisonousToHumans = detailsDto.poisonousToHumans,
            isPoisonousToPets = detailsDto.poisonousToPets,
            imageUrl = detailsDto.defaultImage.bestImageUrl()
        )
        insertPlantWithImageCaching(context, newPlant, onResult)
    }

    fun addManualPlant(
        speciesName: String?,
        customName: String,
        waterDays: Int,
        imageUrl: String?
    ) {
        val newPlant = MyPlantEntity(
            customName = customName,
            speciesName = speciesName,
            scientificName = null,
            family = null,
            origin = null,
            plantType = null,
            sunlight = null,
            attracts = null,
            pruningMonths = null,
            pruningCountAmount = null,
            pruningCountInterval = null,
            growthRate = null,
            soil = null,
            rare = null,
            description = null,
            waterIntervalDays = waterDays,
            nextWateringDate = calculateNextWateringDateMillis(waterDays),
            fruitingSeason = null,
            harvestSeason = null,
            harvestMethod = null,
            isMedicinal = null,
            isPoisonousToHumans = null,
            isPoisonousToPets = null,
            imageUrl = imageUrl
        )
        insertPlant(newPlant)
    }

    fun updatePlantFromEdit(
        plant: MyPlantEntity,
        customName: String,
        waterDays: Int,
        imageUrl: String?
    ) {
        val updatedPlant = plant.copy(
            customName = customName,
            waterIntervalDays = waterDays,
            nextWateringDate = calculateNextWateringDateMillis(waterDays),
            imageUrl = imageUrl
        )
        updatePlant(updatedPlant)
    }

    fun markPlantAsWatered(plantId: Int) {
        viewModelScope.launch {
            repository.markPlantAsWatered(plantId)
        }
    }

    fun markPlantsAsWatered(plantIds: Collection<Int>) {
        viewModelScope.launch {
            plantIds.forEach { repository.markPlantAsWatered(it) }
        }
    }

    fun clearWateringHistory(plantId: Int) {
        viewModelScope.launch {
            repository.clearWateringHistory(plantId)
        }
    }

    fun restoreWateringEvents(events: List<WateringEventEntity>) {
        viewModelScope.launch {
            repository.restoreWateringEvents(events)
        }
    }

    fun recentWateringEvents(plantId: Int, limit: Int = 3): Flow<List<WateringEventEntity>> {
        return repository.getRecentWateringEvents(plantId, limit)
    }

    fun allWateringEvents(plantId: Int): Flow<List<WateringEventEntity>> {
        return repository.getAllWateringEvents(plantId)
    }

    fun cacheRemoteImagesForExistingPlants(context: Context, plants: List<MyPlantEntity>) {
        val uncachedRemotePlants = plants.filter { plant ->
            val imageUrl = plant.imageUrl ?: return@filter false
            val isRemote = imageUrl.startsWith("http://", ignoreCase = true) ||
                imageUrl.startsWith("https://", ignoreCase = true)

            isRemote && plant.id > 0 && !imageCacheAttempts.contains(plant.id)
        }

        if (uncachedRemotePlants.isEmpty()) return

        imageCacheAttempts.addAll(uncachedRemotePlants.map { it.id })
        viewModelScope.launch {
            uncachedRemotePlants.forEach { plant ->
                val cached = repository.cacheRemoteImageForPlant(context, plant)
                if (cached.imageUrl != plant.imageUrl) {
                    repository.updatePlant(cached)
                }
            }
        }
    }

    private fun calculateNextWateringDateMillis(days: Int): Long {
        return System.currentTimeMillis() + (days * 24L * 60L * 60L * 1000L)
    }
}
class GardenViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GardenViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

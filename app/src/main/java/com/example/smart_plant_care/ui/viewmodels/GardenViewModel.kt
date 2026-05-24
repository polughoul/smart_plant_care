package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.data.repository.InsertPlantResult
import com.example.smart_plant_care.data.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GardenViewModel(private val repository: PlantRepository) : ViewModel() {
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

    fun updatePlant(plant: MyPlantEntity) {
        viewModelScope.launch {
            repository.updatePlant(plant)
        }
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

    fun createDemoDuePlant(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.replaceDemoDuePlants(count = 1)
            onComplete?.invoke()
        }
    }

    fun createDemoDuePlants(count: Int, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.replaceDemoDuePlants(count = count)
            onComplete?.invoke()
        }
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

package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class GardenViewModel(private val repository: PlantRepository) : ViewModel() {
    val plantsList: StateFlow<List<MyPlantEntity>> = repository.getAllPlants().stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())

    fun addTestPlant() {
        viewModelScope.launch {
            val testPlant = MyPlantEntity(customName = "Test Plant ${System.currentTimeMillis() % 1000}", speciesName = "Test Species", waterIntervalDays = 7, nextWateringDate = System.currentTimeMillis()
            )
            repository.insertPlant(testPlant)
        }
    }

    fun deletePlant(plantId: Int) {
        viewModelScope.launch {
            repository.deletePlant(plantId)
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
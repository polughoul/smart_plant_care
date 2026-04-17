package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Idle : DetailsUiState
    data object Loading : DetailsUiState
    data class Success(val plant: PlantDetailsDto) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

class DetailsViewModel : ViewModel() {

    private val apiKey = BuildConfig.PERENUAL_API_KEY

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Idle)
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    private var lastPlantId: Int? = null

    fun loadPlantDetails(plantId: Int) {
        if (plantId <= 0) {
            _uiState.value = DetailsUiState.Error("Invalid plant id.")
            return
        }

        lastPlantId = plantId

        if (apiKey.isBlank()) {
            _uiState.value = DetailsUiState.Error("API key is missing. Add PERENUAL_API_KEY to local.properties.")
            return
        }

        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            try {
                val response = RetrofitClient.apiService.getPlantDetails(plantId, apiKey)
                _uiState.value = DetailsUiState.Success(response)
            } catch (_: Exception) {
                _uiState.value = DetailsUiState.Error("Failed to load plant details. Check internet connection and try again.")
            }
        }
    }

    fun retry() {
        val plantId = lastPlantId ?: return
        loadPlantDetails(plantId)
    }
}
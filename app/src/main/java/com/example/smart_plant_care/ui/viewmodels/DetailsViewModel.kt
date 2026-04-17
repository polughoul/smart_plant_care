package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.CareSectionDto
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface DetailsUiState {
    data object Idle : DetailsUiState
    data object Loading : DetailsUiState
    data class Success(
        val plant: PlantDetailsDto,
        val careSections: List<CareSectionDto> = emptyList()
    ) : DetailsUiState
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
                val details = RetrofitClient.apiService.getPlantDetails(plantId, apiKey)

                val shouldLoadCareGuide = details.watering.isNullOrBlank() || details.sunlight.isNullOrEmpty()
                val careSections = if (shouldLoadCareGuide) {
                    runCatching {
                        RetrofitClient.apiService
                            .getSpeciesCareGuide(plantId, apiKey)
                            .data
                            .firstOrNull()
                            ?.section
                            .orEmpty()
                    }.getOrDefault(emptyList())
                } else {
                    emptyList()
                }

                _uiState.value = DetailsUiState.Success(details, careSections)
            } catch (exception: Exception) {
                _uiState.value = DetailsUiState.Error(mapDetailsError(exception))
            }
        }
    }

    fun retry() {
        val plantId = lastPlantId ?: return
        loadPlantDetails(plantId)
    }

    private fun mapDetailsError(exception: Exception): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                401, 403 -> "API key is invalid or unauthorized. Check PERENUAL_API_KEY in local.properties."
                404 -> "Plant details not found (HTTP 404)."
                429 -> "API rate limit reached. Wait a minute and retry."
                in 500..599 -> "Plant API temporary server error (${exception.code()}). Try again later."
                else -> "Plant details request failed (HTTP ${exception.code()})."
            }
            is SocketTimeoutException -> "Request timeout. Check internet connection and retry."
            is IOException -> "No internet connection or network error."
            else -> "Failed to load plant details. Try again."
        }
    }
}
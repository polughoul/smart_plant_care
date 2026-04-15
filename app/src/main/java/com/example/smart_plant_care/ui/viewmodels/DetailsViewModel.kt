package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.PlantDetailsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class DetailsViewModel() : ViewModel() {

    private val apiKey = BuildConfig.PERENUAL_API_KEY

    private val _plantDetails = MutableStateFlow<PlantDetailsDto?>(null)
    val plantDetails: StateFlow<PlantDetailsDto?> = _plantDetails

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPlantDetails(plantId: Int) {
        viewModelScope.launch {
            if (apiKey.isBlank()) {
                _plantDetails.value = null
                _isLoading.value = false
                return@launch
            }

            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getPlantDetails(plantId, apiKey)
                _plantDetails.value = response
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
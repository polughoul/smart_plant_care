package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SearchViewModel: ViewModel() {

    private val apiKey = BuildConfig.PERENUAL_API_KEY

    private val _searchResults = MutableStateFlow<List<ApiPlantDto>>(emptyList())

    val searchResults: StateFlow<List<ApiPlantDto>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchPlants(query: String) {
        if (query.isBlank()) return
        if (apiKey.isBlank()) {
            _searchResults.value = emptyList()
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.searchPlants(query, apiKey)
                _searchResults.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

}
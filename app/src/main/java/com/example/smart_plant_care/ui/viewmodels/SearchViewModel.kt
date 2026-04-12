package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SearchViewModel: ViewModel() {

    private val API_KEY = "sk-4Dpr69dbba30180ee16358"

    private val _searchResults = MutableStateFlow<List<ApiPlantDto>>(emptyList())

    val searchResults: StateFlow<List<ApiPlantDto>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchPlants(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.searchPlants(query, API_KEY)
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
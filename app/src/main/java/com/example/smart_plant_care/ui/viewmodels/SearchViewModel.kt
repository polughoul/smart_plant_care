package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import com.example.smart_plant_care.data.repository.PlantRemoteRepository
import com.example.smart_plant_care.data.repository.RemoteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val plants: List<ApiPlantDto>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel : ViewModel() {

    private val apiKey = BuildConfig.PERENUAL_API_KEY
    private val remoteRepository = PlantRemoteRepository()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var lastQuery: String = ""

    fun searchPlants(query: String) {
        val normalizedQuery = query.trim()
        lastQuery = normalizedQuery

        if (normalizedQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        if (apiKey.isBlank()) {
            _uiState.value = SearchUiState.Error("API key is missing. Add PERENUAL_API_KEY to local.properties.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            when (val result = remoteRepository.searchPlants(normalizedQuery, apiKey)) {
                is RemoteResult.Success -> {
                    val plants = result.data
                    _uiState.value = if (plants.isEmpty()) {
                        SearchUiState.Empty
                    } else {
                        SearchUiState.Success(plants)
                    }
                }

                is RemoteResult.Error -> {
                    _uiState.value = SearchUiState.Error(result.message)
                }
            }
        }
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            searchPlants(lastQuery)
        }
    }
}

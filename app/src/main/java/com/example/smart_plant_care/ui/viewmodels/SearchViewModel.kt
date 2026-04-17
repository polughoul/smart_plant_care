package com.example.smart_plant_care.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.BuildConfig
import com.example.smart_plant_care.data.remote.api.RetrofitClient
import com.example.smart_plant_care.data.remote.dto.ApiPlantDto
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val plants: List<ApiPlantDto>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

class SearchViewModel : ViewModel() {

    private val apiKey = BuildConfig.PERENUAL_API_KEY

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
            try {
                val response = RetrofitClient.apiService.searchPlants(normalizedQuery, apiKey)
                val plants = response.data
                _uiState.value = if (plants.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    SearchUiState.Success(plants)
                }
            } catch (exception: Exception) {
                _uiState.value = SearchUiState.Error(mapSearchError(exception))
            }
        }
    }

    fun retryLastSearch() {
        if (lastQuery.isNotBlank()) {
            searchPlants(lastQuery)
        }
    }

    private fun mapSearchError(exception: Exception): String {
        return when (exception) {
            is HttpException -> when (exception.code()) {
                401, 403 -> "API key is invalid or unauthorized. Check PERENUAL_API_KEY in local.properties."
                404 -> "Plant API endpoint not found (HTTP 404)."
                429 -> "API rate limit reached. Wait a minute and retry."
                in 500..599 -> "Plant API temporary server error (${exception.code()}). Try again later."
                else -> "Plant API request failed (HTTP ${exception.code()})."
            }
            is SocketTimeoutException -> "Request timeout. Check internet connection and retry."
            is IOException -> "No internet connection or network error."
            else -> "Failed to load plants. Try again."
        }
    }
}
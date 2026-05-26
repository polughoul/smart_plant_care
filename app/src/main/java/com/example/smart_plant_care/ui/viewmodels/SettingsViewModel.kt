package com.example.smart_plant_care.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smart_plant_care.data.preferences.ThemeMode
import com.example.smart_plant_care.data.preferences.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true
)

class SettingsViewModel(
    private val preferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesDataStore.preferencesFlow.collect { preferences ->
                _uiState.value = SettingsUiState(
                    themeMode = preferences.themeMode,
                    notificationsEnabled = preferences.notificationsEnabled
                )
            }
        }
    }

    fun onThemeModeChange(themeMode: ThemeMode) {
        viewModelScope.launch {
            preferencesDataStore.setThemeMode(themeMode)
        }
    }

    fun onNotificationsToggle(enabled: Boolean) {
        _uiState.update { current -> current.copy(notificationsEnabled = enabled) }
        viewModelScope.launch {
            preferencesDataStore.setNotificationsEnabled(enabled)
        }
    }
}

class SettingsViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                UserPreferencesDataStore(appContext)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


package com.example.smart_plant_care.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesDataStore = UserPreferencesDataStore(application.applicationContext)

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


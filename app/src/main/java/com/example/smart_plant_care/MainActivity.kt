package com.example.smart_plant_care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smart_plant_care.data.local.db.AppDatabase
import com.example.smart_plant_care.data.preferences.ThemeMode
import com.example.smart_plant_care.data.repository.PlantRepository
import com.example.smart_plant_care.notifications.PlantReminderScheduler
import com.example.smart_plant_care.ui.main.MainScreen
import com.example.smart_plant_care.ui.theme.Smart_Plant_CareTheme
import com.example.smart_plant_care.ui.viewmodels.SettingsViewModel
import com.example.smart_plant_care.ui.viewmodels.SettingsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlantReminderScheduler.createNotificationChannel(applicationContext)
        enableEdgeToEdge()
        setContent {
            val db = remember { AppDatabase.getDatabase(applicationContext) }
            val repository = remember { PlantRepository(db.plantDao(), db.wateringEventDao()) }
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(applicationContext)
            )
            val settingsUiState by settingsViewModel.uiState.collectAsState()

            val darkTheme = when (settingsUiState.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            Smart_Plant_CareTheme(
                darkTheme = darkTheme,
                dynamicColor = settingsUiState.themeMode == ThemeMode.SYSTEM
            ) {
                MainScreen(
                    repository = repository,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

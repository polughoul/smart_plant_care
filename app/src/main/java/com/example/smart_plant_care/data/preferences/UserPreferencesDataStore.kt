package com.example.smart_plant_care.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataStore(private val context: Context) {

    private object Keys {
        val THEME_MODE: Preferences.Key<String> = stringPreferencesKey("theme_mode")
        val NOTIFICATIONS_ENABLED: Preferences.Key<Boolean> = booleanPreferencesKey("notifications_enabled")
        val GARDEN_SORT_OPTION: Preferences.Key<String> = stringPreferencesKey("garden_sort_option")
    }

    val preferencesFlow: Flow<UserPreferences> = context.userPreferencesDataStore.data.map { preferences ->
        val themeMode = preferences[Keys.THEME_MODE]
            ?.let { raw -> runCatching { ThemeMode.valueOf(raw) }.getOrNull() }
            ?: ThemeMode.SYSTEM
        val gardenSortOption = preferences[Keys.GARDEN_SORT_OPTION]
            ?.let { raw -> runCatching { GardenSortOption.valueOf(raw) }.getOrNull() }
            ?: GardenSortOption.NEXT_WATERING

        UserPreferences(
            themeMode = themeMode,
            notificationsEnabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: true,
            gardenSortOption = gardenSortOption
        )
    }

    suspend fun setThemeMode(themeMode: ThemeMode) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = themeMode.name
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setGardenSortOption(option: GardenSortOption) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.GARDEN_SORT_OPTION] = option.name
        }
    }
}


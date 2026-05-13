package com.example.smart_plant_care.data.preferences

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class GardenSortOption(val labelRes: Int) {
    NEXT_WATERING(com.example.smart_plant_care.R.string.my_garden_sort_next_watering),
    NAME(com.example.smart_plant_care.R.string.my_garden_sort_name)
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val gardenSortOption: GardenSortOption = GardenSortOption.NEXT_WATERING
)

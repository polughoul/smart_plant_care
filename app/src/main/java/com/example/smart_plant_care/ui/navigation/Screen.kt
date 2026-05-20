package com.example.smart_plant_care.ui.navigation

import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object MyGarden : Screen("my_garden")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object Help : Screen("help")

    object Details : Screen("details/{apiId}"){
        fun createRoute(apiId:Int) = "details/$apiId"
    }
    object Edit : Screen("edit/{speciesName}/{defaultWater}") {
        fun createRoute(speciesName: String, defaultWater: Int): String {
            val encodedName = encode(speciesName, "UTF-8")
            return "edit/$encodedName/$defaultWater"
        }
    }

    object EditExisting : Screen("edit_existing/{plantId}") {
        fun createRoute(plantId: Int): String = "edit_existing/$plantId"
    }

    object GardenDetails : Screen("garden_details/{plantId}") {
        fun createRoute(plantId: Int): String = "garden_details/$plantId"
    }

    object PlantNotes : Screen("plant_notes/{plantId}") {
        fun createRoute(plantId: Int): String = "plant_notes/$plantId"
    }

    object WateringHistory : Screen("watering_history/{plantId}") {
        fun createRoute(plantId: Int): String = "watering_history/$plantId"
    }

    object PlantDetailsEdit : Screen("plant_details_edit/{plantId}") {
        fun createRoute(plantId: Int): String = "plant_details_edit/$plantId"
    }

}
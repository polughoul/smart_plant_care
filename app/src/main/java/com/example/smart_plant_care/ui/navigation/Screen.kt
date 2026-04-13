package com.example.smart_plant_care.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object MyGarden : Screen("my_garden")
    object Search : Screen("search")
    object Settings : Screen("settings")

    object Details : Screen("details/{apiId}"){
        fun createRoute(apiId:Int) = "details/$apiId"
    }
    object Edit : Screen("edit/{speciesName}") {
        fun createRoute(speciesName: String): String {
            val encodedName = URLEncoder.encode(speciesName, StandardCharsets.UTF_8.toString())
            return "edit/$encodedName"
        }
    }

}
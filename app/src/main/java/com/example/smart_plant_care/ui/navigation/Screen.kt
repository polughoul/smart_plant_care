package com.example.smart_plant_care.ui.navigation

import java.net.URLEncoder.encode
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object MyGarden : Screen("my_garden")
    object Search : Screen("search")
    object Settings : Screen("settings")

    object Details : Screen("details/{apiId}"){
        fun createRoute(apiId:Int) = "details/$apiId"
    }
    object Edit : Screen("edit/{speciesName}/{defaultWater}") {
        fun createRoute(speciesName: String, defaultWater: Int): String {
            val encodedName = encode(speciesName, "UTF-8")
            return "edit/$encodedName/$defaultWater"
        }
    }

}
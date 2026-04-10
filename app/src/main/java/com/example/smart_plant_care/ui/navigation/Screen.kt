package com.example.smart_plant_care.ui.navigation

sealed class Screen(val route: String) {
    object MyGarden : Screen("my_garden")
    object Search : Screen("search")
    object Settings : Screen("settings")

    object Details : Screen("details/{apiId}"){
        fun createRoute(apiId:Int) = "details/$apiId"
    }
    object Edit : Screen("edit") {
        fun createRoute(plantId: Int) = "edit/$plantId"
    }

}
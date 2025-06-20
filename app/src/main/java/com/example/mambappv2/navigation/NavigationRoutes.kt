package com.example.mambappv2.navigation

sealed class NavigationRoutes(val route: String) {
    object Home : NavigationRoutes("home")
    object Form : NavigationRoutes("form")
    object Detail : NavigationRoutes("detail")
}

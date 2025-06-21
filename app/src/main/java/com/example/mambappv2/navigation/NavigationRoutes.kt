package com.example.mambappv2.navigation

/**
 * Centraliza todas las rutas de navegación usadas en la app.
 * Ideal para mantener orden y facilitar escalabilidad.
 */
sealed class NavigationRoutes(val route: String) {

    // Pantallas principales
    object Home : NavigationRoutes("home")
    object List : NavigationRoutes("list")

    // Monitoreo (creación y edición)
    object Form : NavigationRoutes("form")           // Nuevo monitoreo
    object FormEdit : NavigationRoutes("formEdit")   // Edición de monitoreo

    // Detalle
    object Detail : NavigationRoutes("detail")
}

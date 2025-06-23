// ListScreenUpdateHelper.kt
package com.example.mambappv2.utils

/**
 * Utility functions for usage count display in ResourceCards
 */
object ListScreenUpdateHelper {
    
    fun getUsageMessage(count: Int): String {
        return when (count) {
            0 -> "Sin usos registrados"
            1 -> "Usado en 1 monitoreo"
            else -> "Usado en $count monitoreos"
        }
    }

    fun getDeletionErrorMessage(entityName: String, count: Int): String {
        return "No se puede eliminar este $entityName porque está siendo usado en $count monitoreo(s)."
    }
} 
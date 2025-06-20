// FechaSection.kt
package com.example.mambappv2.ui.components.monitoreo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.mambappv2.ui.components.inputs.DateInputField

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FechaSection(
    fechaRealizado: String,
    onFechaRealizadoChange: (String) -> Unit,
    fechaPresentado: String,
    onFechaPresentadoChange: (String) -> Unit,
    fechaCobrado: String,
    onFechaCobradoChange: (String) -> Unit
) {
    Column {
        Text("🗓️ Fechas", style = MaterialTheme.typography.titleMedium)

        DateInputField(
            label = "Fecha Realizado",
            date = fechaRealizado,
            onDateChange = onFechaRealizadoChange
        )

        DateInputField(
            label = "Fecha Presentado",
            date = fechaPresentado,
            onDateChange = onFechaPresentadoChange
        )

        DateInputField(
            label = "Fecha Cobrado",
            date = fechaCobrado,
            onDateChange = onFechaCobradoChange
        )
    }
}

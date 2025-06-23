// FechaSection.kt
package com.example.mambappv2.ui.components.monitoreo

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ModernDateField(
            label = "Fecha Realizado",
            date = fechaRealizado,
            onDateChange = onFechaRealizadoChange,
            icon = Icons.Default.CalendarToday,
            isRequired = true
        )

        ModernDateField(
            label = "Fecha Presentado",
            date = fechaPresentado,
            onDateChange = onFechaPresentadoChange,
            icon = Icons.Default.Schedule,
            isRequired = false
        )

        ModernDateField(
            label = "Fecha Cobrado",
            date = fechaCobrado,
            onDateChange = onFechaCobradoChange,
            icon = Icons.Default.Payment,
            isRequired = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ModernDateField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isRequired: Boolean
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val today = LocalDate.now()
    val initialDate = if (date.isNotBlank()) LocalDate.parse(date, formatter) else today

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                    onDateChange(selectedDate.format(formatter))
                    showDialog = false
                },
                initialDate.year,
                initialDate.monthValue - 1,
                initialDate.dayOfMonth
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
                setOnCancelListener { showDialog = false }
            }.show()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label moderno
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = Color.White.copy(alpha = 0.9f)
            )
            Text(
                text = label + if (isRequired) " *" else "",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Campo moderno
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    val displayText = if (date.isNotBlank()) {
                        LocalDate.parse(date, formatter).format(displayFormatter)
                    } else {
                        "Seleccionar fecha"
                    }
                    Text(
                        displayText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Botón de borrar para todas las fechas (con diferentes estilos)
                if (date.isNotBlank()) {
                    Card(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRequired) 
                                Color.White.copy(alpha = 0.1f) 
                            else 
                                Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        IconButton(
                            onClick = { onDateChange("") },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = if (isRequired) 
                                    "Limpiar fecha (requerida)" 
                                else 
                                    "Limpiar fecha",
                                tint = if (isRequired)
                                    Color.White.copy(alpha = 0.6f)
                                else
                                    Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// DateInputField.kt
package com.example.mambappv2.ui.components.inputs

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateInputField(
    label: String,
    date: String,
    onDateChange: (String) -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                val displayText = if (date.isNotBlank()) {
                    LocalDate.parse(date, formatter).format(displayFormatter)
                } else {
                    "Seleccionar fecha"
                }
                Text(displayText, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { onDateChange("") },
                enabled = date.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar fecha",
                    tint = if (date.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}

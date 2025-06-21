package com.example.mambappv2.ui.state

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MonitoreoFormState {
    // Fechas
    var fechaRealizado by mutableStateOf(LocalDate.now().toString())
    var fechaPresentado by mutableStateOf("")
    var fechaCobrado by mutableStateOf("")

    // Paciente
    var dniPaciente by mutableStateOf("")
    var showDialogTipo by mutableStateOf("")
    val camposDialog = mutableStateListOf<Pair<String, MutableState<String>>>()

    fun setCamposDialog(vararg keys: String) {
        camposDialog.clear()
        camposDialog.addAll(keys.map { it to mutableStateOf("") })
    }

    // Profesionales
    var selectedMedicoId by mutableStateOf(-1)
    var selectedTecnicoId by mutableStateOf(-1)
    var selectedSolicitanteId by mutableStateOf(-1)

    // Lugar
    var selectedLugarId by mutableStateOf(-1)

    // Patología
    var selectedPatologiaId by mutableStateOf(-1)

    // Detalle clínico
    var anestesia by mutableStateOf("")
    var complicacion by mutableStateOf(false)
    var detalleComplicacion by mutableStateOf("")
    var cambioMotor by mutableStateOf("")

    // Equipo
    var selectedEquipoId by mutableStateOf(-1)
}

package com.example.mambappv2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.mambappv2.data.entities.Monitoreo
import com.example.mambappv2.navigation.NavigationRoutes
import com.example.mambappv2.ui.screens.*
import com.example.mambappv2.ui.theme.MambAppV2Theme
import com.example.mambappv2.viewmodel.*
import com.example.mambappv2.di.AppContainer

class MainActivity : ComponentActivity() {
    private lateinit var appContainer: AppContainer

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(applicationContext)

        setContent {
            MambAppV2Theme {
                val navController = rememberNavController()

                // ViewModels
                val monitoreoVM = viewModel { MonitoreoViewModel(appContainer.monitoreoRepository) }
                val pacienteVM = viewModel { PacienteViewModel(appContainer.pacienteRepository) }
                val medicoVM = viewModel { MedicoViewModel(appContainer.medicoRepository) }
                val tecnicoVM = viewModel { TecnicoViewModel(appContainer.tecnicoRepository) }
                val lugarVM = viewModel { LugarViewModel(appContainer.lugarRepository) }
                val patologiaVM = viewModel { PatologiaViewModel(appContainer.patologiaRepository) }
                val solicitanteVM = viewModel { SolicitanteViewModel(appContainer.solicitanteRepository) }
                val equipoVM = viewModel { EquipoViewModel(appContainer.equipoRepository) }

                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.Home.route
                ) {

                    // Home
                    composable(NavigationRoutes.Home.route) {
                        HomeScreen(
                            onNavigateToNew = { navController.navigate(NavigationRoutes.Form.route) },
                            onNavigateToList = { navController.navigate(NavigationRoutes.List.route) }
                        )
                    }

                    // Nuevo monitoreo
                    composable(NavigationRoutes.Form.route) {
                        MonitoreoScreen(
                            monitoreo = null,
                            viewModel = monitoreoVM,
                            pacienteViewModel = pacienteVM,
                            medicoViewModel = medicoVM,
                            tecnicoViewModel = tecnicoVM,
                            lugarViewModel = lugarVM,
                            patologiaViewModel = patologiaVM,
                            solicitanteViewModel = solicitanteVM,
                            equipoViewModel = equipoVM,
                            onBack = { navController.popBackStack() },
                            onSaveSuccess = { navController.popBackStack() }
                        )
                    }

                    // Edición de monitoreo
                    composable(NavigationRoutes.FormEdit.route) {
                        val monitoreoEdit = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<Monitoreo>("editarMonitoreo")

                        MonitoreoScreen(
                            monitoreo = monitoreoEdit,
                            viewModel = monitoreoVM,
                            pacienteViewModel = pacienteVM,
                            medicoViewModel = medicoVM,
                            tecnicoViewModel = tecnicoVM,
                            lugarViewModel = lugarVM,
                            patologiaViewModel = patologiaVM,
                            solicitanteViewModel = solicitanteVM,
                            equipoViewModel = equipoVM,
                            onBack = { navController.popBackStack() },
                            onSaveSuccess = { navController.popBackStack() }
                        )
                    }

                    // Detalle de monitoreo
                    composable(NavigationRoutes.Detail.route) {
                        val monitoreo = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<Monitoreo>("monitoreo")

                        DetailScreen(
                            monitoreo = monitoreo,
                            lugarViewModel = lugarVM,
                            patologiaViewModel = patologiaVM,
                            medicoViewModel = medicoVM,
                            tecnicoViewModel = tecnicoVM,
                            solicitanteViewModel = solicitanteVM,
                            monitoreoViewModel = monitoreoVM,
                            pacienteViewModel = pacienteVM,
                            equipoViewModel = equipoVM,
                            onBack = { navController.popBackStack() },
                            onEdit = { selected ->
                                navController.currentBackStackEntry?.savedStateHandle
                                    ?.set("editarMonitoreo", selected)
                                navController.navigate(NavigationRoutes.FormEdit.route)
                            }
                        )
                    }

                    // Lista de monitoreos
                    composable(NavigationRoutes.List.route) {
                        ListScreen(
                            viewModel = monitoreoVM,
                            lugarViewModel = lugarVM,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

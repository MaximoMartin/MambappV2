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
import com.example.mambappv2.ui.screens.resources.ResourceMenuScreen
import com.example.mambappv2.ui.screens.resources.equipo.EquipoListScreen
import com.example.mambappv2.ui.screens.resources.lugar.LugarListScreen
import com.example.mambappv2.ui.screens.resources.medico.MedicoListScreen
import com.example.mambappv2.ui.screens.resources.paciente.PacienteListScreen
import com.example.mambappv2.ui.screens.resources.patologia.PatologiaListScreen
import com.example.mambappv2.ui.screens.resources.solicitante.SolicitanteListScreen
import com.example.mambappv2.ui.screens.resources.tecnico.TecnicoListScreen
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

                NavHost(navController = navController, startDestination = NavigationRoutes.Home.route) {

                    // Home
                    composable(NavigationRoutes.Home.route) {
                        HomeScreen(
                            onNavigateToNew = { navController.navigate(NavigationRoutes.Form.route) },
                            onNavigateToList = { navController.navigate(NavigationRoutes.List.route) },
                            onNavigateToResources = { navController.navigate(NavigationRoutes.ResourceMenu.route) }
                        )
                    }

                    // Formulario: nuevo monitoreo
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

                    // Formulario: editar monitoreo
                    composable(NavigationRoutes.FormEdit.route) {
                        val monitoreoEdit = navController.previousBackStackEntry
                            ?.savedStateHandle?.get<Monitoreo>("editarMonitoreo")

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
                            ?.savedStateHandle?.get<Monitoreo>("monitoreo")

                        DetailScreen(
                            monitoreo = monitoreo,
                            lugarViewModel = lugarVM,
                            patologiaViewModel = patologiaVM,
                            medicoViewModel = medicoVM,
                            tecnicoViewModel = tecnicoVM,
                            solicitanteViewModel = solicitanteVM,
                            pacienteViewModel = pacienteVM,
                            equipoViewModel = equipoVM,
                            monitoreoViewModel = monitoreoVM,
                            onBack = { navController.popBackStack() },
                            onEdit = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("editarMonitoreo", it)
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

                    // Menú de selección de recursos
                    composable(NavigationRoutes.ResourceMenu.route) {
                        ResourceMenuScreen(navController = navController)
                    }

                    // Pantallas de listado por entidad
                    composable(NavigationRoutes.PacienteList.route) {
                        PacienteListScreen(navController = navController, viewModel = pacienteVM)
                    }
                    composable(NavigationRoutes.MedicoList.route) {
                        MedicoListScreen(navController = navController, viewModel = medicoVM)
                    }
                    composable(NavigationRoutes.TecnicoList.route) {
                        TecnicoListScreen(navController = navController, viewModel = tecnicoVM)
                    }
                    composable(NavigationRoutes.SolicitanteList.route) {
                        SolicitanteListScreen(navController = navController, viewModel = solicitanteVM)
                    }
                    composable(NavigationRoutes.LugarList.route) {
                        LugarListScreen(navController = navController, viewModel = lugarVM)
                    }
                    composable(NavigationRoutes.PatologiaList.route) {
                        PatologiaListScreen(navController = navController, viewModel = patologiaVM)
                    }
                    composable(NavigationRoutes.EquipoList.route) {
                        EquipoListScreen(navController = navController, viewModel = equipoVM)
                    }
                }
            }
        }
    }
}

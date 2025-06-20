package com.example.mambappv2

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
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

                // ViewModels manuales
                val monitoreoVM: MonitoreoViewModel = viewModel(factory = MonitoreoViewModelFactory(appContainer.monitoreoRepository))
                val pacienteVM: PacienteViewModel = viewModel(factory = PacienteViewModelFactory(appContainer.pacienteRepository))
                val medicoVM: MedicoViewModel = viewModel(factory = MedicoViewModelFactory(appContainer.medicoRepository))
                val tecnicoVM: TecnicoViewModel = viewModel(factory = TecnicoViewModelFactory(appContainer.tecnicoRepository))
                val lugarVM: LugarViewModel = viewModel(factory = LugarViewModelFactory(appContainer.lugarRepository))
                val patologiaVM: PatologiaViewModel = viewModel(factory = PatologiaViewModelFactory(appContainer.patologiaRepository))
                val solicitanteVM: SolicitanteViewModel = viewModel(factory = SolicitanteViewModelFactory(appContainer.solicitanteRepository))

                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.Home.route
                ) {
                    composable(NavigationRoutes.Home.route) {
                        HomeScreen(
                            onNavigateToNew = { navController.navigate(NavigationRoutes.Form.route) },
                            onNavigateToList = { navController.navigate(NavigationRoutes.Detail.route) }
                        )
                    }

                    composable(NavigationRoutes.Form.route) {
                        MonitoreoScreen(
                            viewModel = monitoreoVM,
                            pacienteViewModel = pacienteVM,
                            medicoViewModel = medicoVM,
                            tecnicoViewModel = tecnicoVM,
                            lugarViewModel = lugarVM,
                            patologiaViewModel = patologiaVM,
                            solicitanteViewModel = solicitanteVM,
                            onBack = { navController.popBackStack() },
                            onSaveSuccess = { navController.popBackStack() }
                        )
                    }

                    composable(NavigationRoutes.Detail.route) {
                        val monitoreo =
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<Monitoreo>("monitoreo")

                        DetailScreen(
                            monitoreo = monitoreo,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

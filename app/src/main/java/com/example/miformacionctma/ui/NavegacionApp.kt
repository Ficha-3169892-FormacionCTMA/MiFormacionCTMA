package com.example.miformacionctma.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaDetalleActividad

@Composable
fun NavegacionApp(viewModel: ActividadesViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "lista_actividades"
    ) {
        composable("lista_actividades") {
            PantallaActividades(
                actividades = uiState.actividades,
                onActividadClick = { id ->
                    viewModel.seleccionarActividad(id)
                    navController.navigate("detalle_actividad/$id")
                },
                onToggleCompletada = { id ->
                    viewModel.alternarEstadoActividad(id)
                }
            )
        }

        composable(
            route = "detalle_actividad/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.IntType })
        ) {
            val actividad = uiState.actividadSeleccionada

            PantallaDetalleActividad(
                actividad = actividad,
                onVolver = { navController.popBackStack() },
                onToggleCompletada = { id ->
                    viewModel.alternarEstadoActividad(id)
                }
            )
        }
    }
}
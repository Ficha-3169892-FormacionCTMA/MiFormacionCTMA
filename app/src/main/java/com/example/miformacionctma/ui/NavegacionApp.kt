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
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
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
                onCrearActividadClick = {
                    navController.navigate("crear_actividad")
                }
            )
        }

        composable("crear_actividad") {
            PantallaCrearActividad(
                onVolver = { navController.popBackStack() },
                onGuardar = { titulo, descripcion ->
                    viewModel.agregarActividad(titulo, descripcion)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "detalle_actividad/{actividadId}",
            arguments = listOf(navArgument("actividadId") { type = NavType.IntType })
        ) {
            PantallaDetalleActividad(
                actividad = uiState.actividadSeleccionada,
                onVolver = { navController.popBackStack() },
                onToggleCompletada = { id ->
                    val actual = uiState.actividadSeleccionada?.progreso ?: 0f
                    val nuevoProgreso = if (actual >= 1.0f) 0.0f else 1.0f
                    viewModel.actualizarProgresoActividad(id, nuevoProgreso)
                }
            )
        }
    }
}
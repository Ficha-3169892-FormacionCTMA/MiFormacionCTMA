package com.example.miformacionctma.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miformacionctma.ui.screens.*
import com.example.miformacionctma.ui.states.ListadoUiState
import com.example.miformacionctma.ui.states.OperacionUiState
import kotlinx.serialization.Serializable

// Definimos los destinos como objetos o clases serializables
@Serializable
object ListaRoute

@Serializable
data class DetalleRoute(val actividadId: String)

@Serializable
object CrearRoute

@Composable
fun AppNavigation(
    viewModel: ActividadesViewModel = viewModel(factory = ActividadesViewModel.Factory),
) {
    val navController = rememberNavController()
    val listadoUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val preferencias by viewModel.preferencias.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = ListaRoute,
    ) {
        composable<ListaRoute> {
            PantallaActividades(
                listadoUiState = listadoUiState,
                searchQuery = searchQuery,
                onSearchChange = viewModel::buscar,
                prioridadSeleccionada = preferencias.filtroPrioridad,
                onPrioridadFilterClick = viewModel::filtrarPorPrioridad,
                ordenadoPorVencimiento = preferencias.ordenadoPorVencimiento,
                onSortClick = viewModel::alternarOrden,
                onActividadClick = { actividad ->
                    viewModel.seleccionarActividad(actividad.id)
                    navController.navigate(DetalleRoute(actividad.id.toString()))
                },
                onCrearClick = {
                    navController.navigate(CrearRoute)
                },
            )
        }

        composable<DetalleRoute> { backStackEntry ->
            val route: DetalleRoute = backStackEntry.toRoute()
            val actividadSeleccionada by viewModel.actividadSeleccionada.collectAsStateWithLifecycle()
            
            LaunchedEffect(route.actividadId) {
                Log.d("Analytics", "Detalle de Actividad #${route.actividadId} Visualizada")
                viewModel.seleccionarActividad(route.actividadId.toLongOrNull() ?: -1L)
            }

            val detalleUiState = remember(actividadSeleccionada, route.actividadId) {
                if (actividadSeleccionada != null) {
                    DetalleUiState.Exito(actividadSeleccionada!!)
                } else {
                    DetalleUiState.NoEncontrada(route.actividadId)
                }
            }

            PantallaDetalle(
                uiState = detalleUiState,
                onVolverClick = { navController.popBackStack() },
            )
        }

        composable<CrearRoute> {
            val operacionState by viewModel.operacion.collectAsStateWithLifecycle()

            LaunchedEffect(operacionState) {
                if (operacionState is OperacionUiState.Exitosa) {
                    navController.popBackStack()
                    viewModel.resetOperacion()
                }
            }

            PantallaCrearActividad(
                operacionUiState = operacionState,
                onActividadGuardada = { titulo, descripcion, progreso, prioridad, fechaMillis ->
                    viewModel.guardarActividad(titulo, descripcion, progreso, prioridad, fechaMillis)
                },
                onVolverClick = { 
                    navController.popBackStack()
                    viewModel.resetOperacion()
                }
            )
        }
    }
}

package com.example.miformacionctma.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.screens.*
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
    viewModel: ActividadesViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = ListaRoute
    ) {
        composable<ListaRoute> {
            PantallaActividades(
                actividades = uiState.actividadesVisibles,
                searchQuery = uiState.searchQuery,
                onSearchChange = { viewModel.buscar(it) },
                prioridadSeleccionada = uiState.filtroPrioridad,
                onPrioridadFilterClick = { viewModel.filtrarPorPrioridad(it) },
                ordenadoPorVencimiento = uiState.ordenadoPorVencimiento,
                onSortClick = { viewModel.alternarOrden() },
                onActividadClick = { actividad ->
                    viewModel.seleccionarActividad(actividad.id)
                    navController.navigate(DetalleRoute(actividad.id.toString()))
                },
                onCrearClick = {
                    navController.navigate(CrearRoute)
                }
            )
        }

        composable<DetalleRoute> { backStackEntry ->
            val route: DetalleRoute = backStackEntry.toRoute()
            
            LaunchedEffect(route.actividadId) {
                Log.d("Analytics", "Detalle de Actividad #${route.actividadId} Visualizada")
            }

            // Usamos el estado del ViewModel para el detalle (UDF)
            val detalleState = remember(route.actividadId, uiState.actividadesVisibles) {
                val idLong = route.actividadId.toLongOrNull()
                val actividad = uiState.actividadesVisibles.find { it.id == idLong }
                if (actividad != null) {
                    DetalleUiState.Exito(actividad)
                } else {
                    DetalleUiState.NoEncontrada(route.actividadId)
                }
            }

            PantallaDetalle(
                uiState = detalleState,
                onVolverClick = { navController.popBackStack() }
            )
        }

        composable<CrearRoute> {
            LaunchedEffect(Unit) {
                Log.d("Analytics", "Pantalla de Creación Visualizada")
            }

            PantallaCrearActividad(
                onActividadGuardada = { titulo, descripcion, progreso, prioridad, fechaMillis ->
                    viewModel.guardarActividad(titulo, descripcion, progreso, prioridad, fechaMillis)
                    navController.popBackStack()
                },
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}

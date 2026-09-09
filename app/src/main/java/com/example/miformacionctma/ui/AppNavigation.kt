package com.example.miformacionctma.ui

import android.provider.Settings
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.miformacionctma.ui.screens.*
import com.example.miformacionctma.ui.states.OperacionUiState
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
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
    val context = LocalContext.current
    val navController = rememberNavController()
    
    // Detección de "Reducir Movimiento" (HU 12)
    val reduceMotion = remember {
        val scale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
        scale == 0f
    }
    val animDuration = if (reduceMotion) 0 else 300

    val listadoUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val preferencias by viewModel.preferencias.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = ListaRoute,
        enterTransition = { fadeIn(animationSpec = tween(animDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(animDuration)) }
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
                onActualizarActividad = viewModel::actualizarProgreso
            )
        }

        composable<DetalleRoute>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(animDuration)) +
                fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(animDuration)) +
                fadeOut(animationSpec = tween(animDuration))
            }
        ) { backStackEntry ->
            val route: DetalleRoute = backStackEntry.toRoute()
            val actividadSeleccionada by viewModel.actividadSeleccionada.collectAsStateWithLifecycle()
            val operacionState by viewModel.operacion.collectAsStateWithLifecycle()

            LaunchedEffect(route.actividadId) {
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
                onGuardarProgreso = { nuevoProgreso ->
                    actividadSeleccionada?.let {
                        viewModel.guardarActividad(
                            it.titulo, it.descripcion ?: "", nuevoProgreso, it.prioridad, 
                            System.currentTimeMillis() + (it.diasRestantes.toLong() * 24 * 60 * 60 * 1000)
                        )
                    }
                },
                operacionUiState = operacionState
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

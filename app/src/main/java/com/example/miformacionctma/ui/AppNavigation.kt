package com.example.miformacionctma.ui

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.miformacionctma.ui.viewmodel.EvidenciaViewModel
import kotlinx.serialization.Serializable

// Destinos
@Serializable object ListaRoute
@Serializable data class DetalleRoute(val actividadId: String)
@Serializable data class FormularioRoute(val actividadId: String? = null)

@Composable
fun AppNavigation(
    actividadesViewModel: ActividadesViewModel = viewModel(factory = ActividadesViewModel.Factory),
    evidenciaViewModel: EvidenciaViewModel = viewModel(factory = EvidenciaViewModel.Factory),
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    val reduceMotion = remember {
        val scale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
        scale == 0f
    }
    val animDuration = if (reduceMotion) 0 else 300

    val listadoUiState by actividadesViewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by actividadesViewModel.searchQuery.collectAsStateWithLifecycle()
    val preferencias by actividadesViewModel.preferencias.collectAsStateWithLifecycle()

    // Gestión de Permisos de Notificaciones (Semana 9 - Requisito 5)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        // CA-07: Denegar no bloquea la aplicación.
    }

    NavHost(
        navController = navController,
        startDestination = ListaRoute,
        enterTransition = { fadeIn(animationSpec = tween(durationMillis = animDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(durationMillis = animDuration)) },
    ) {
        composable<ListaRoute> {
            PantallaActividades(
                listadoUiState = listadoUiState,
                searchQuery = searchQuery,
                onSearchChange = actividadesViewModel::buscar,
                prioridadSeleccionada = preferencias.filtroPrioridad,
                onPrioridadFilterClick = actividadesViewModel::filtrarPorPrioridad,
                ordenadoPorVencimiento = preferencias.ordenadoPorVencimiento,
                onSortClick = actividadesViewModel::alternarOrden,
                onActividadClick = { actividad ->
                    actividadesViewModel.seleccionarActividad(id = actividad.id)
                    navController.navigate(route = DetalleRoute(actividadId = actividad.id.toString()))
                },
                onCrearClick = { navController.navigate(route = FormularioRoute()) },
                onActualizarActividad = actividadesViewModel::actualizarProgreso,
                onEliminarActividad = actividadesViewModel::eliminarActividad,
                onRestaurarActividad = actividadesViewModel::restaurarActividad,
                recordatoriosActivos = preferencias.recordatoriosActivos,
                onToggleRecordatorios = { activo ->
                    if (activo && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    actividadesViewModel.actualizarRecordatorios(activo)
                },
            )
        }

        composable<DetalleRoute>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(durationMillis = animDuration)) +
                fadeIn(animationSpec = tween(durationMillis = animDuration))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(durationMillis = animDuration)) +
                fadeOut(animationSpec = tween(durationMillis = animDuration))
            },
        ) { backStackEntry ->
            val route: DetalleRoute = backStackEntry.toRoute()
            val idLong = route.actividadId.toLongOrNull() ?: -1L
            
            val actividadSeleccionada by actividadesViewModel.actividadSeleccionada.collectAsStateWithLifecycle()
            val operacionState by actividadesViewModel.operacion.collectAsStateWithLifecycle()
            val evidencias by evidenciaViewModel.evidenciasPorActividad(idLong).collectAsStateWithLifecycle(initialValue = emptyList())
            val errorFeedback by evidenciaViewModel.errorFeedback.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = route.actividadId) {
                actividadesViewModel.seleccionarActividad(id = idLong)
            }

            val detalleUiState = remember(actividadSeleccionada, route.actividadId) {
                if (actividadSeleccionada != null) {
                    DetalleUiState.Exito(actividad = actividadSeleccionada!!)
                } else {
                    DetalleUiState.NoEncontrada(id = route.actividadId)
                }
            }

            PantallaDetalle(
                uiState = detalleUiState,
                evidencias = evidencias,
                errorFeedback = errorFeedback,
                onClearError = evidenciaViewModel::clearError,
                onVolverClick = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(FormularioRoute(actividadId = route.actividadId))
                },
                onGuardarProgreso = { nuevoProgreso ->
                    actividadSeleccionada?.let {
                        // FIX: Se usa actualizarProgreso en lugar de guardarActividad para evitar duplicados
                        actividadesViewModel.actualizarProgreso(it.id, nuevoProgreso)
                    }
                },
                onAgregarEvidencia = { uri -> evidenciaViewModel.agregarEvidencia(idLong, uri) },
                onEliminarEvidencia = evidenciaViewModel::eliminarEvidencia,
                onReintentarEvidencia = evidenciaViewModel::reintentarSubida,
                operacionUiState = operacionState,
            )
        }

        composable<FormularioRoute> { backStackEntry ->
            val route: FormularioRoute = backStackEntry.toRoute()
            val operacionState by actividadesViewModel.operacion.collectAsStateWithLifecycle()
            val actividadAEditar by actividadesViewModel.actividadSeleccionada.collectAsStateWithLifecycle()

            LaunchedEffect(route.actividadId) {
                if (route.actividadId != null) {
                    actividadesViewModel.seleccionarActividad(route.actividadId.toLong())
                } else {
                    actividadesViewModel.seleccionarActividad(null)
                }
            }

            LaunchedEffect(key1 = operacionState) {
                if (operacionState is OperacionUiState.Exitosa) {
                    navController.popBackStack()
                    actividadesViewModel.resetOperacion()
                }
            }

            if (route.actividadId == null || actividadAEditar != null) {
                PantallaCrearActividad(
                    actividadAEditar = if (route.actividadId != null) actividadAEditar else null,
                    operacionUiState = operacionState,
                    onActividadGuardada = { id, titulo, descripcion, progreso, prioridad, fechaMillis ->
                        actividadesViewModel.guardarActividad(
                            id = id,
                            titulo = titulo,
                            descripcion = descripcion,
                            progreso = progreso,
                            prioridad = prioridad,
                            fechaMillis = fechaMillis
                        )
                    }
                ) {
                    navController.popBackStack()
                    actividadesViewModel.resetOperacion()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

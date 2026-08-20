package com.example.miformacionctma.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.screens.DetalleUiState
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.screens.PantallaCrearActividad
import com.example.miformacionctma.ui.screens.PantallaDetalle
import kotlinx.serialization.Serializable
import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Definimos los destinos como objetos o clases serializables
@Serializable
object ListaRoute

@Serializable
data class DetalleRoute(val actividadId: String)

@Serializable
object CrearRoute

@Composable
fun AppNavigation(actividadesIniciales: List<ActividadFormativa>) {
    val navController = rememberNavController()
    
    // Implementación de Saver basada en JSON para persistencia robusta
    val actividadesSaver = Saver<List<ActividadFormativa>, String>(
        save = { Json.encodeToString(it) },
        restore = { Json.decodeFromString(it) }
    )

    // Elevamos el estado de la lista con persistencia ante recreación (Sección 4 del Material)
    var listaActividades by rememberSaveable(stateSaver = actividadesSaver) {
        mutableStateOf(actividadesIniciales)
    }

    NavHost(
        navController = navController,
        startDestination = ListaRoute
    ) {
        composable<ListaRoute> {
            // Efecto Lateral: Registro de analíticas (Sección 9 del Material)
            LaunchedEffect(Unit) {
                Log.d("Analytics", "Pantalla de Lista Visualizada")
            }

            PantallaActividades(
                actividades = listaActividades,
                onActividadClick = { actividad ->
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

            val uiState = remember(route.actividadId, listaActividades) {
                val idLong = route.actividadId.toLongOrNull()
                val actividad = listaActividades.find { it.id == idLong }
                if (actividad != null) {
                    DetalleUiState.Exito(actividad)
                } else {
                    DetalleUiState.NoEncontrada(route.actividadId)
                }
            }

            PantallaDetalle(
                uiState = uiState,
                onVolverClick = { navController.popBackStack() }
            )
        }

        composable<CrearRoute> {
            LaunchedEffect(Unit) {
                Log.d("Analytics", "Pantalla de Creación Visualizada")
            }

            PantallaCrearActividad(
                onActividadGuardada = { titulo, descripcion, progreso, prioridad, fechaMillis ->
                    val hoy = java.util.Calendar.getInstance().apply {
                        set(java.util.Calendar.HOUR_OF_DAY, 0)
                        set(java.util.Calendar.MINUTE, 0)
                        set(java.util.Calendar.SECOND, 0)
                        set(java.util.Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    
                    val diasRestantes = ((fechaMillis - hoy) / (1000 * 60 * 60 * 24)).toInt()

                    val nuevaActividad = ActividadFormativa(
                        id = (listaActividades.maxOfOrNull { it.id } ?: 0L) + 1L,
                        titulo = titulo,
                        descripcion = descripcion,
                        progreso = progreso,
                        prioridad = prioridad,
                        diasRestantes = diasRestantes,
                        estado = ReglasActividad.obtenerEstado(progreso, diasRestantes)
                    )
                    
                    listaActividades = listaActividades + nuevaActividad
                    navController.popBackStack()
                },
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}

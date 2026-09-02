package com.example.miformacionctma.ui.navigation

import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.screens.ContenidoAdaptable
import com.example.miformacionctma.ui.screens.CrearScreen
import com.example.miformacionctma.ui.screens.DetalleScreen

// Centralización de rutas tipo String
object Rutas {
    const val LISTA = "lista"
    const val CREAR = "crear"
    const val DETALLE = "detalle/{actividadId}"

    fun crearRutaDetalle(id: String) = "detalle/$id"
}

@Composable
fun MiFormacionNavHost(
    actividades: List<ActividadFormativa>,
    onGuardarNuevaActividad: (ActividadFormativa) -> Unit,
    onUpdateActividad: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    
    // Mitigación: Detección de configuración de "Reducir Movimiento" (Criterio 4)
    val reduceMotion = remember {
        val scale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f
        )
        scale == 0f
    }

    // Tiempos de animación entre 200ms y 300ms (Criterio 2)
    val animDuration = if (reduceMotion) 0 else 300

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTA,
        modifier = modifier
    ) {
        // Destino 1: Lista
        composable(
            route = Rutas.LISTA,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            ContenidoAdaptable(
                actividades = actividades,
                onCrearActividadClick = {
                    navController.navigate(Rutas.CREAR)
                },
                onActividadClick = { id ->
                    navController.navigate(Rutas.crearRutaDetalle(id))
                }
            )
        }

        // Destino 2: Crear
        composable(
            route = Rutas.CREAR,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) {
            CrearScreen(
                onGuardar = { nuevaActividad ->
                    onGuardarNuevaActividad(nuevaActividad)
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Destino 3: Detalle (Criterio 1: Animación de transición)
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("actividadId") { type = NavType.StringType }),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(animDuration)
                ) + fadeIn(animationSpec = tween(animDuration))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(animDuration)
                ) + fadeOut(animationSpec = tween(animDuration))
            }
        ) { backStackEntry ->
            val actividadId = backStackEntry.arguments?.getString("actividadId") ?: ""
            DetalleScreen(
                actividadId = actividadId,
                actividades = actividades,
                onGuardarProgreso = { actividadActualizada ->
                    onUpdateActividad(actividadActualizada)
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}

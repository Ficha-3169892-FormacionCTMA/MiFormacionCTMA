package com.example.miformacionctma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    NavHost(
        navController = navController,
        startDestination = Rutas.LISTA,
        modifier = modifier
    ) {
        // Destino 1: Lista
        composable(Rutas.LISTA) {
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
        composable(Rutas.CREAR) {
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

        // Destino 3: Detalle
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("actividadId") { type = NavType.StringType })
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

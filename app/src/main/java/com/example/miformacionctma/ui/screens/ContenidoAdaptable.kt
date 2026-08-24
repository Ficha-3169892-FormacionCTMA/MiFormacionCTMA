package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.components.EncabezadoFormacion
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoAdaptable(
    actividades: List<ActividadFormativa>,
    modifier: Modifier = Modifier
) {
    // Cálculo dinámico de métricas para el nuevo panel del encabezado
    val totalActividades = actividades.size
    val completadas = actividades.count { it.progreso >= 100 }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Formación CTMA") })
        }
    ) { paddingValues ->
        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Umbral de adaptabilidad definido en 600.dp[cite: 1]
            val esPantallaAncha = this.maxWidth >= 600.dp

            Column(modifier = Modifier.fillMaxSize()) {
                // Encabezado actualizado con métricas reales
                EncabezadoFormacion(
                    nombre = "Aprendiz",
                    totalActividades = totalActividades,
                    completadas = completadas
                )

                if (actividades.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay actividades registradas.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else if (esPantallaAncha) {
                    // Vista para pantallas anchas (Cuadrícula de 2 columnas)[cite: 1]
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = actividades, key = { it.id }) { actividad -> // Clave estable por id[cite: 1]
                            TarjetaActividad(
                                actividad = actividad,
                                onActividadClick = { }
                            )
                        }
                    }
                } else {
                    // Vista para teléfonos (Lista vertical)[cite: 1]
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(items = actividades, key = { it.id }) { actividad -> // Clave estable por id[cite: 1]
                            TarjetaActividad(
                                actividad = actividad,
                                onActividadClick = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
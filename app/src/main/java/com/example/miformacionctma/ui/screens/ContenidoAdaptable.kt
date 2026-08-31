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
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.ui.components.EncabezadoFormacion
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoAdaptable(
    actividades: List<Actividad>,
    onActividadClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Cálculo dinámico de métricas para el encabezado (progreso es Float 0..1)
    val totalActividades = actividades.size
    val completadas = actividades.count { it.progreso >= 1.0f || it.completada }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Formación CTMA") })
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Umbral de adaptabilidad definido en 600.dp
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
                    // Vista para pantallas anchas (Cuadrícula de 2 columnas)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = actividades, key = { it.id }) { actividad ->
                            TarjetaActividad(
                                actividad = actividad,
                                onClick = { onActividadClick(actividad.id) }
                            )
                        }
                    }
                } else {
                    // Vista para teléfonos (Lista vertical)
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(items = actividades, key = { it.id }) { actividad ->
                            TarjetaActividad(
                                actividad = actividad,
                                onClick = { onActividadClick(actividad.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

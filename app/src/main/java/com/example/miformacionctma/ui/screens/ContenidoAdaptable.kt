package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.components.EncabezadoFormacion
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoAdaptable(
    actividades: List<ActividadFormativa>,
    onCrearActividadClick: () -> Unit,
    onActividadClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalActividades = actividades.size
    val completadas = actividades.count { it.progreso >= 100 }

    val configuration = LocalConfiguration.current
    val esPantallaAncha = configuration.screenWidthDp.dp >= 600.dp

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Formación CTMA") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearActividadClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar nueva actividad"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                // Vista en cuadrícula para pantallas anchas
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = actividades, key = { it.id }) { actividad ->
                        TarjetaActividad(
                            actividad = actividad,
                            onActividadClick = { onActividadClick(actividad.id.toString()) }
                        )
                    }
                }
            } else {
                // Vista en lista vertical para dispositivos móviles
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(items = actividades, key = { it.id }) { actividad ->
                        TarjetaActividad(
                            actividad = actividad,
                            onActividadClick = { onActividadClick(actividad.id.toString()) }
                        )
                    }
                }
            }
        }
    }
}
@file:Suppress("SpellCheckingInspection", "UnusedBoxWithConstraintsScope")

package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA") }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            val esPantallaAncha = this.maxWidth >= 600.dp

            if (actividades.isEmpty()) {
                EstadoVacio()
            } else if (esPantallaAncha) {
                CuadriculaActividades(actividades = actividades, onActividadClick = onActividadClick)
            } else {
                ListaActividades(actividades = actividades, onActividadClick = onActividadClick)
            }
        }
    }
}

@Composable
fun ListaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(
            items = actividades,
            key = { it.id }
        ) { actividad ->
            TarjetaActividad(actividad = actividad, onActividadClick = onActividadClick)
        }
    }
}

@Composable
fun CuadriculaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = actividades,
            key = { it.id }
        ) { actividad ->
            TarjetaActividad(actividad = actividad, onActividadClick = onActividadClick)
        }
    }
}

@Composable
fun EstadoVacio() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No hay actividades formativas registradas.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
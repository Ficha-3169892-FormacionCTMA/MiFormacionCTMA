@file:Suppress("SpellCheckingInspection", "UnusedBoxWithConstraintsScope")

package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    prioridadSeleccionada: Prioridad?,
    onPrioridadFilterClick: (Prioridad?) -> Unit,
    ordenadoPorVencimiento: Boolean,
    onSortClick: () -> Unit,
    onActividadClick: (ActividadFormativa) -> Unit,
    onCrearClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Mi Formación CTMA") },
                    actions = {
                        IconButton(onClick = onSortClick) {
                            Icon(
                                Icons.Default.Menu, 
                                contentDescription = "Ordenar",
                                tint = if (ordenadoPorVencimiento) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                
                // Barra de Búsqueda (HU 5)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                // Filtros de Prioridad (HU 7)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        FilterChip(
                            selected = prioridadSeleccionada == prioridad,
                            onClick = { 
                                if (prioridadSeleccionada == prioridad) onPrioridadFilterClick(null)
                                else onPrioridadFilterClick(prioridad)
                            },
                            label = { Text(prioridad.name) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearClick) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Actividad")
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            val esPantallaAncha = maxWidth >= 600.dp

            if (actividades.isEmpty()) {
                EstadoVacio(hayFiltros = (searchQuery.isNotEmpty()) || (prioridadSeleccionada != null))
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
    onActividadClick: (ActividadFormativa) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(
            items = actividades,
            key = { it.id },
        ) { actividad ->
            TarjetaActividad(actividad = actividad, onActividadClick = onActividadClick)
        }
    }
}

@Composable
fun CuadriculaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
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
fun EstadoVacio(hayFiltros: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hayFiltros) "No se encontraron actividades con estos filtros." 
                  else "No hay actividades formativas registradas.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

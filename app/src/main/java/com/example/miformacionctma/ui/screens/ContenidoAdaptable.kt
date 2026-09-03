package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.components.EncabezadoFormacion
import com.example.miformacionctma.ui.components.GuiaEstadoVacio
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenidoAdaptable(
    actividades: List<ActividadFormativa>,
    onCrearActividadClick: () -> Unit,
    onActividadClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Criterio 3: Conservación de posición
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    var textoBusqueda by remember { mutableStateOf("") }

    // Aplicar ordenamiento lógico según ReglasActividad (Limpia advertencia en domain)
    val actividadesOrdenadas = remember(actividades) {
        ReglasActividad.ordenarActividades(actividades)
    }

    // Aplicar búsqueda (Limpia advertencia en domain)
    val actividadesFiltradas = remember(actividadesOrdenadas, textoBusqueda) {
        ReglasActividad.buscarPorTitulo(actividadesOrdenadas, textoBusqueda)
    }

    val configuration = LocalConfiguration.current
    val esPantallaAncha = configuration.screenWidthDp.dp >= 600.dp

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Mi Formación CTMA") })
        },
        floatingActionButton = {
            if (actividades.isNotEmpty()) {
                FloatingActionButton(onClick = onCrearActividadClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar nueva actividad"
                    )
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (actividades.isEmpty()) {
                GuiaEstadoVacio(
                    onCrearClick = onCrearActividadClick
                )
            } else {
                EncabezadoFormacion(
                    nombre = "Aprendiz",
                    actividades = actividadesOrdenadas
                )

                // Barra de búsqueda integrada para añadir valor y limpiar alertas
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar por título...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )

                if (esPantallaAncha) {
                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = actividadesFiltradas, key = { it.id }) { actividad ->
                            TarjetaActividad(
                                actividad = actividad,
                                onActividadClick = { onActividadClick(actividad.id.toString()) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items = actividadesFiltradas, key = { it.id }) { actividad ->
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
}

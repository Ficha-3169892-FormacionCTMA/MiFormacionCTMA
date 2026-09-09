@file:Suppress("SpellCheckingInspection", "UnusedBoxWithConstraintsScope")

package com.example.miformacionctma.ui.screens

import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.components.DashboardStats
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.states.ListadoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    listadoUiState: ListadoUiState,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    prioridadSeleccionada: Prioridad?,
    onPrioridadFilterClick: (Prioridad?) -> Unit,
    ordenadoPorVencimiento: Boolean,
    onSortClick: () -> Unit,
    onActividadClick: (ActividadFormativa) -> Unit,
    onCrearClick: () -> Unit,
    onActualizarActividad: (Long, Int) -> Unit = { _, _ -> },
) {
    val contexto = LocalContext.current

    var actividadEdicion by remember { mutableStateOf<ActividadFormativa?>(null) }
    var textoIngresado by remember { mutableStateOf("") }

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
                                tint = if (ordenadoPorVencimiento) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    },
                )

                // Barra de Búsqueda
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
                    shape = MaterialTheme.shapes.medium,
                )

                // Filtros de Prioridad
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Prioridad.entries.forEach { prioridad ->
                        FilterChip(
                            selected = prioridadSeleccionada == prioridad,
                            onClick = {
                                if (prioridadSeleccionada == prioridad) onPrioridadFilterClick(null)
                                else onPrioridadFilterClick(prioridad)
                            },
                            label = { Text(prioridad.name) },
                        )
                    }
                }

                if (listadoUiState is ListadoUiState.Contenido) {
                    if ((searchQuery.isNotEmpty()) || (prioridadSeleccionada != null)) {
                        Text(
                            text = "Mostrando ${listadoUiState.actividades.size} actividades",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearClick) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Actividad")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (listadoUiState) {
                is ListadoUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ListadoUiState.Error -> {
                    ErrorState(
                        mensaje = listadoUiState.mensaje,
                        onReintentar = onSortClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ListadoUiState.Vacio -> {
                    EstadoVacio(
                        hayFiltros = (searchQuery.isNotEmpty()) || (prioridadSeleccionada != null),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ListadoUiState.Contenido -> {
                    ContenidoLista(actividades = listadoUiState.actividades) { actividad ->
                        actividadEdicion = actividad
                        textoIngresado = actividad.progreso.toString()
                    }
                }
            }
        }
    }

    actividadEdicion?.let { actividad ->
        AlertDialog(
            onDismissRequest = { actividadEdicion = null },
            title = { Text("Actualizar Avance") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = actividad.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Días restantes: ${actividad.diasRestantes}")

                    OutlinedTextField(
                        value = textoIngresado,
                        onValueChange = { textoIngresado = it },
                        label = { Text("Nuevo Porcentaje (0 - 100)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
            },
            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Logro CTMA: He completado ${actividad.progreso}% de '${actividad.titulo}'.")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            contexto.startActivity(shareIntent)
                        }
                    ) {
                        Text("Compartir")
                    }

                    if (actividad.enlaceEvidencia != null) {
                        TextButton(
                            onClick = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, actividad.enlaceEvidencia.toUri())
                                contexto.startActivity(browserIntent)
                            }
                        ) {
                            Text("Evidencia")
                        }
                    }

                    TextButton(
                        onClick = {
                            onActividadClick(actividad)
                            actividadEdicion = null
                        }
                    ) {
                        Text("Detalles")
                    }

                    Button(
                        onClick = {
                            val progresoInt = textoIngresado.toIntOrNull() ?: actividad.progreso
                            onActualizarActividad(actividad.id, progresoInt)
                            actividadEdicion = null
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { actividadEdicion = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ContenidoLista(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        val esPantallaAncha = maxWidth >= 600.dp
        if (esPantallaAncha) {
            CuadriculaActividades(actividades = actividades, onActividadClick = onActividadClick)
        } else {
            ListaActividades(actividades = actividades, onActividadClick = onActividadClick)
        }
    }
}

@Composable
fun ListaActividades(
    actividades: List<ActividadFormativa>,
    onActividadClick: (ActividadFormativa) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            DashboardStats(actividades = actividades)
        }
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
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            DashboardStats(actividades = actividades)
        }
        items(
            items = actividades,
            key = { it.id },
        ) { actividad ->
            TarjetaActividad(actividad = actividad, onActividadClick = onActividadClick)
        }
    }
}

@Composable
fun EstadoVacio(hayFiltros: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (hayFiltros) "No se encontraron actividades con estos filtros."
            else "No hay actividades formativas registradas.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
fun ErrorState(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onReintentar) {
            Text("Reintentar")
        }
    }
}

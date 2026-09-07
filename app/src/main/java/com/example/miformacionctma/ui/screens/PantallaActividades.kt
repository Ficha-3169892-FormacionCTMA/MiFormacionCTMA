@file:Suppress("SpellCheckingInspection", "UnusedBoxWithConstraintsScope")

package com.example.miformacionctma.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.ListadoUiState
import com.example.miformacionctma.ui.OperacionUiState
import com.example.miformacionctma.ui.components.DashboardStats
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel

@Composable
fun PantallaActividadesRoute(
    viewModel: ActividadesViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val operacionState by viewModel.operacion.collectAsStateWithLifecycle()
    val textoBusqueda by viewModel.textoBusqueda.collectAsStateWithLifecycle()

    PantallaActividadesScreen(
        uiState = uiState,
        operacionState = operacionState,
        textoBusqueda = textoBusqueda,
        onBusquedaChange = viewModel::cambiarBusqueda,
        onGuardarProgreso = viewModel::actualizarProgreso,
        onReiniciarOperacion = viewModel::reiniciarEstadoOperacion,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividadesScreen(
    uiState: ListadoUiState,
    operacionState: OperacionUiState,
    textoBusqueda: String,
    onBusquedaChange: (String) -> Unit,
    onGuardarProgreso: (ActividadFormativa, Int) -> Unit,
    onReiniciarOperacion: () -> Unit
) {
    val contexto = LocalContext.current

    var actividadSeleccionada by remember { mutableStateOf<ActividadFormativa?>(null) }
    var textoIngresado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA") },
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

            Column(modifier = Modifier.fillMaxSize()) {

                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = onBusquedaChange,
                    label = { Text("Buscar actividad...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    singleLine = true
                )

                when (operacionState) {
                    is OperacionUiState.Fallida -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = operacionState.mensaje,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    is OperacionUiState.Exitosa -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "Progreso actualizado correctamente",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    OperacionUiState.EnCurso -> {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    OperacionUiState.Inactiva -> {}
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (uiState) {
                        is ListadoUiState.Cargando -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        is ListadoUiState.Vacio -> {
                            EstadoVacio()
                        }
                        is ListadoUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error: ${uiState.mensaje}", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        is ListadoUiState.Contenido -> {
                            val actividades = uiState.actividades
                            if (esPantallaAncha) {
                                CuadriculaActividades(
                                    actividades = actividades,
                                    onActividadClick = { actividad ->
                                        onReiniciarOperacion()
                                        actividadSeleccionada = actividad
                                        textoIngresado = actividad.progreso.toString()
                                    }
                                )
                            } else {
                                ListaActividades(
                                    actividades = actividades,
                                    onActividadClick = { actividad ->
                                        onReiniciarOperacion()
                                        actividadSeleccionada = actividad
                                        textoIngresado = actividad.progreso.toString()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    actividadSeleccionada?.let { actividad ->
        AlertDialog(
            onDismissRequest = { actividadSeleccionada = null },
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
                        singleLine = true
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
                            Text("Ver Evidencia")
                        }
                    }

                    Button(
                        onClick = {
                            val nuevoValor = textoIngresado.toIntOrNull() ?: -1
                            onGuardarProgreso(actividad, nuevoValor)
                            actividadSeleccionada = null
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { actividadSeleccionada = null }) {
                    Text("Cancelar")
                }
            }
        )
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
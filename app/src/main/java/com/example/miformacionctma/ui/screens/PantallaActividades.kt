@file:Suppress("SpellCheckingInspection", "UnusedBoxWithConstraintsScope")

package com.example.miformacionctma.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.core.net.toUri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.components.DashboardStats
import com.example.miformacionctma.ui.components.TarjetaActividad
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import com.example.miformacionctma.ui.viewmodel.EstadoProgresoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<ActividadFormativa>,
    viewModel: ActividadesViewModel = remember { ActividadesViewModel() },
) {
    val estadoUI by viewModel.estadoUI.collectAsState()
    val contexto = LocalContext.current

    // Estados para controlar el diálogo flotante de edición al hacer clic en una tarjeta
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
                // Mensajes de feedback (Error o Éxito según las reglas de negocio)
                when (val estado = estadoUI) {
                    is EstadoProgresoUI.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = estado.mensaje,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    is EstadoProgresoUI.Exito -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = estado.mensaje,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    EstadoProgresoUI.Reposo -> {}
                }

                // Contenido principal (Lista, Cuadrícula o Estado Vacío)
                Box(modifier = Modifier.weight(1f)) {
                    if (actividades.isEmpty()) {
                        EstadoVacio()
                    } else if (esPantallaAncha) {
                        CuadriculaActividades(
                            actividades = actividades,
                        ) { actividad ->
                            viewModel.reiniciarEstado()
                            actividadSeleccionada = actividad
                            textoIngresado = actividad.progreso.toString()
                        }
                    } else {
                        ListaActividades(
                            actividades = actividades,
                        ) { actividad ->
                            viewModel.reiniciarEstado()
                            actividadSeleccionada = actividad
                            textoIngresado = actividad.progreso.toString()
                        }
                    }
                }
            }
        }
    }

    // Diálogo emergente para modificar el avance al hacer clic en cualquier tarjeta
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
                            viewModel.actualizarProgreso(actividad, nuevoValor)
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
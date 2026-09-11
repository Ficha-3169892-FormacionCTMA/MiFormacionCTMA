package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.ui.states.OperacionUiState

sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Exito(val actividad: ActividadFormativa) : DetalleUiState
    data class NoEncontrada(val id: String) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    uiState: DetalleUiState,
    onVolverClick: () -> Unit,
    onGuardarProgreso: (Int) -> Unit = {},
    operacionUiState: OperacionUiState = OperacionUiState.Inactiva,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is DetalleUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetalleUiState.Exito -> {
                    DetalleContenido(
                        actividad = uiState.actividad,
                        onGuardarProgreso = onGuardarProgreso,
                        estaGuardando = operacionUiState is OperacionUiState.EnCurso
                    )
                }
                is DetalleUiState.NoEncontrada -> {
                    Text(
                        text = "Actividad #${uiState.id} no encontrada",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DetalleUiState.Error -> {
                    Text(
                        text = uiState.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DetalleContenido(
    actividad: ActividadFormativa,
    onGuardarProgreso: (Int) -> Unit,
    estaGuardando: Boolean,
) {
    var editandoProgreso by remember { mutableStateOf(value = false) }
    var nuevoProgreso by remember { mutableFloatStateOf(actividad.progreso.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = actividad.titulo, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        HorizontalDivider()

        InfoItem(label = "Descripción", value = actividad.descripcion ?: "Sin descripción")
        InfoItem(label = "Prioridad", value = actividad.prioridad.name)
        InfoItem(label = "Días Restantes", value = actividad.diasRestantes.toString())
        InfoItem(label = "Estado", value = actividad.estado.name)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Progreso Actual: ${actividad.progreso}%", style = MaterialTheme.typography.titleMedium)
        
        // Barra visual (HU 11)
        LinearProgressIndicator(
            progress = { actividad.progreso / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = if (actividad.progreso >= 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )

        if (!editandoProgreso) {
            Button(onClick = { editandoProgreso = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Modificar Progreso")
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Nuevo Progreso: ${nuevoProgreso.toInt()}%", style = MaterialTheme.typography.bodyLarge)
                    
                    // Slider para control granular (HU 11)
                    Slider(
                        value = nuevoProgreso,
                        onValueChange = { nuevoProgreso = it },
                        valueRange = 0f..100f,
                        steps = 10
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { 
                                onGuardarProgreso(nuevoProgreso.toInt())
                                editandoProgreso = false
                            },
                            enabled = !estaGuardando,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (estaGuardando) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            else Text("Guardar")
                        }
                        OutlinedButton(
                            onClick = { editandoProgreso = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
        
        if (actividad.enlaceEvidencia != null) {
            OutlinedButton(
                onClick = { /* Lógica de apertura de link HU 13 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Evidencia")
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

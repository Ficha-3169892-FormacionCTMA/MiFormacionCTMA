package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa

// 1. Estados explícitos de la UI para la pantalla de detalle
sealed interface DetalleUiState {
    data object Cargando : DetalleUiState
    data class Exito(val actividad: ActividadFormativa) : DetalleUiState
    data class NoEncontrada(val id: String) : DetalleUiState
}

// 2. Pantalla Stateful
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    actividadId: String,
    actividades: List<ActividadFormativa>,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Resolver el estado de la actividad según el ID
    var uiState by remember { mutableStateOf<DetalleUiState>(DetalleUiState.Cargando) }

    LaunchedEffect(actividadId, actividades) {
        val encontrada = actividades.find { it.id == actividadId.toLong() }
        uiState = if (encontrada != null) {
            DetalleUiState.Exito(encontrada)
        } else {
            DetalleUiState.NoEncontrada(actividadId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DetalleUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetalleUiState.Exito -> {
                    DetalleActividadContent(
                        actividad = state.actividad,
                        onVolver = onVolver
                    )
                }
                is DetalleUiState.NoEncontrada -> {
                    DetalleNoEncontradoContent(
                        id = state.id,
                        onVolver = onVolver
                    )
                }
            }
        }
    }
}

// 3. Contenido en caso de Éxito (Stateless)
@Composable
fun DetalleActividadContent(
    actividad: ActividadFormativa,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = actividad.titulo,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(actividad.prioridad.name) }
                    )
                }

                HorizontalDivider()

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (actividad.descripcion.isNotBlank()) actividad.descripcion else "Sin descripción adicional.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Progreso actual: ${actividad.progreso}%",
                    style = MaterialTheme.typography.bodySmall
                )
                LinearProgressIndicator(
                    progress = { actividad.progreso / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regresar")
        }
    }
}

// 4. Contenido en caso de ID No Encontrado (Stateless)
@Composable
fun DetalleNoEncontradoContent(
    id: String,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error actividad no encontrada",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Actividad no encontrada",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No se pudo recuperar la información para el ID:\n$id",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVolver) {
            Text("Volver a la lista")
        }
    }
}
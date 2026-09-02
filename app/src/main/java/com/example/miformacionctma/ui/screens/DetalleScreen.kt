package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
    onGuardarProgreso: (ActividadFormativa) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Resolver el estado de la actividad según el ID
    var uiState by remember { mutableStateOf<DetalleUiState>(DetalleUiState.Cargando) }

    // Usamos un efecto para buscar la actividad cada vez que cambie la lista o el ID
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
                        onGuardarProgreso = onGuardarProgreso,
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
    onGuardarProgreso: (ActividadFormativa) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editandoProgreso by rememberSaveable { mutableStateOf(false) }
    var nuevoProgresoStr by rememberSaveable { mutableStateOf(actividad.progreso.toString()) }
    var errorProgreso by rememberSaveable { mutableStateOf<String?>(null) }

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
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(actividad.prioridad.name) }
                    )
                }

                // Información de Fecha Límite
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fecha límite: ${actividad.fechaLimite ?: "${actividad.diasRestantes} días restantes"}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
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

                HorizontalDivider()

                // SECCIÓN DE PROGRESO (Criterio 1, 2, 3)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val estaCompletada = actividad.progreso >= 100
                    Column {
                        Text(
                            text = "Progreso: ${actividad.progreso}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (estaCompletada) "Estado: Completado" else "Estado: En progreso",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (estaCompletada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    if (!editandoProgreso) {
                        IconButton(onClick = { editandoProgreso = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar progreso")
                        }
                    }
                }

                if (editandoProgreso) {
                    OutlinedTextField(
                        value = nuevoProgresoStr,
                        onValueChange = { 
                            if (it.all { char -> char.isDigit() } && it.length <= 3) {
                                nuevoProgresoStr = it
                                errorProgreso = null
                            }
                        },
                        label = { Text("Porcentaje (0-100)") },
                        suffix = { Text("%") },
                        isError = errorProgreso != null,
                        supportingText = { if (errorProgreso != null) Text(errorProgreso!!) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { 
                            editandoProgreso = false
                            nuevoProgresoStr = actividad.progreso.toString()
                            errorProgreso = null
                        }) {
                            Text("Cancelar")
                        }
                        Button(onClick = {
                            val valor = nuevoProgresoStr.toIntOrNull()
                            // Validación de rango (Criterio 4)
                            if (valor == null || valor < 0 || valor > 100) {
                                errorProgreso = "Ingresa un valor entre 0 y 100"
                            } else {
                                // Sincronización de interfaz (Mitigación de riesgo)
                                onGuardarProgreso(actividad.copy(progreso = valor))
                                editandoProgreso = false
                                errorProgreso = null
                            }
                        }) {
                            Text("Guardar")
                        }
                    }
                }

                // Barra visual (Criterio 2)
                LinearProgressIndicator(
                    progress = { actividad.progreso / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (actividad.progreso >= 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
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

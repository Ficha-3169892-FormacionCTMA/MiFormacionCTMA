package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.utils.DateUtils
import java.time.LocalDate
import java.time.ZoneId

// 1. Estado UI del formulario
data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val prioridad: String = "MEDIA",
    val fechaLimiteMillis: Long? = null,
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val fechaError: String? = null,
    val intentoGuardar: Boolean = false
) {
    val puedeGuardar: Boolean
        get() = titulo.trim().length in 3..80 && 
                descripcion.length <= 240 && 
                fechaLimiteMillis != null
}

// 2. Pantalla contenedora de Estado (Stateful Screen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearScreen(
    onGuardar: (ActividadFormativa) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Para corregir las alertas "Assigned value is never read", usamos MutableState directamente.
    // Esto asegura que el compilador entienda la mutación del estado dentro de las lambdas.
    val tituloState = rememberSaveable { mutableStateOf("") }
    val descripcionState = rememberSaveable { mutableStateOf("") }
    val prioridadState = rememberSaveable { mutableStateOf("MEDIA") }
    val fechaLimiteMillisState = rememberSaveable { mutableStateOf<Long?>(null) }
    val intentoGuardarState = rememberSaveable { mutableStateOf(false) }

    // Evaluaciones dinámicas de validación centralizadas en el domain
    val uiState = remember(
        tituloState.value, 
        descripcionState.value, 
        prioridadState.value, 
        fechaLimiteMillisState.value, 
        intentoGuardarState.value
    ) {
        FormularioActividadUiState(
            titulo = tituloState.value,
            descripcion = descripcionState.value,
            prioridad = prioridadState.value,
            fechaLimiteMillis = fechaLimiteMillisState.value,
            tituloError = ReglasActividad.validarTitulo(tituloState.value, intentoGuardarState.value),
            descripcionError = ReglasActividad.validarDescripcion(descripcionState.value),
            fechaError = ReglasActividad.validarFecha(fechaLimiteMillisState.value, intentoGuardarState.value),
            intentoGuardar = intentoGuardarState.value
        )
    }

    val showDatePickerState = remember { mutableStateOf(false) }

    if (showDatePickerState.value) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Bloquea fechas pasadas (Criterio de Aceptación 2)
                    val hoyMillis = LocalDate.now(ZoneId.systemDefault())
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    return utcTimeMillis >= hoyMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerState.value = false },
            confirmButton = {
                TextButton(onClick = {
                    fechaLimiteMillisState.value = datePickerState.selectedDateMillis
                    showDatePickerState.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerState.value = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver a la lista"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        FormularioActividadContent(
            uiState = uiState,
            onTituloChange = { tituloState.value = it.take(80) },
            onDescripcionChange = { descripcionState.value = it.take(240) },
            onPrioridadChange = { prioridadState.value = it },
            onFechaClick = { showDatePickerState.value = true },
            onLimpiarFecha = { fechaLimiteMillisState.value = null },
            onGuardarClick = {
                intentoGuardarState.value = true
                if (uiState.puedeGuardar) {
                    val nuevaActividad = ActividadFormativa(
                        id = System.currentTimeMillis(),
                        titulo = tituloState.value.trim(),
                        descripcion = descripcionState.value.trim(),
                        prioridad = Prioridad.valueOf(prioridadState.value),
                        progreso = 0,
                        diasRestantes = DateUtils.calcularDiasRestantes(fechaLimiteMillisState.value),
                        // Guardamos la fecha formateada para mostrarla en la tarjeta
                        fechaLimite = DateUtils.formatToDisplay(fechaLimiteMillisState.value)
                    )
                    onGuardar(nuevaActividad)
                }
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// 3. Componente de presentación sin estado (Stateless Content)
@Composable
fun FormularioActividadContent(
    uiState: FormularioActividadUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onPrioridadChange: (String) -> Unit,
    onFechaClick: () -> Unit,
    onLimpiarFecha: () -> Unit,
    onGuardarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo: Título
        OutlinedTextField(
            value = uiState.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título de la evidencia *") },
            supportingText = {
                Text(uiState.tituloError ?: "${uiState.titulo.length}/80")
            },
            isError = uiState.tituloError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        // Campo: Descripción
        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción o detalles") },
            supportingText = {
                Text(uiState.descripcionError ?: "${uiState.descripcion.length}/240")
            },
            isError = uiState.descripcionError != null,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        // Campo: Fecha Límite
        OutlinedTextField(
            value = DateUtils.formatToDisplay(uiState.fechaLimiteMillis),
            onValueChange = { },
            readOnly = true,
            enabled = false, // Para que el clic lo maneje el Modifier.clickable
            label = { Text("Fecha límite *") },
            placeholder = { Text("DD/MM/AAAA") },
            supportingText = {
                if (uiState.fechaError != null) {
                    Text(uiState.fechaError, color = MaterialTheme.colorScheme.error)
                } else {
                    Text("Formato: DD/MM/AAAA")
                }
            },
            isError = uiState.fechaError != null,
            trailingIcon = {
                if (uiState.fechaLimiteMillis != null) {
                    IconButton(onClick = onLimpiarFecha) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar fecha")
                    }
                } else {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (uiState.fechaError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onFechaClick() }
        )

        // Selección de Prioridad
        Text(
            text = "Prioridad",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Prioridad.entries.forEach { nivel ->
                FilterChip(
                    selected = uiState.prioridad == nivel.name,
                    onClick = { onPrioridadChange(nivel.name) },
                    label = { Text(nivel.name) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón de Acción
        Button(
            onClick = onGuardarClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Actividad")
        }
    }
}

package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.states.OperacionUiState
import java.time.LocalDate
import java.util.Locale
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearActividad(
    operacionUiState: OperacionUiState,
    onActividadGuardada: (titulo: String, descripcion: String, progreso: Int, prioridad: Prioridad, fechaMillis: Long) -> Unit,
    onVolverClick: () -> Unit,
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var progreso by rememberSaveable { mutableIntStateOf(0) }
    var prioridad by rememberSaveable { mutableStateOf(Prioridad.MEDIA) }
    var fechaSeleccionadaMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    
    val showDatePickerState = remember { mutableStateOf(false) }

    val uiState = FormularioActividadUiState(
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        prioridad = prioridad,
        fechaSeleccionadaMillis = fechaSeleccionadaMillis,
        intentoGuardar = intentoGuardar,
        tituloError = if (intentoGuardar) ReglasActividad.validarTitulo(titulo, true) else null,
        descripcionError = if (intentoGuardar || (descripcion.length > 240)) ReglasActividad.validarDescripcion(descripcion) else null,
        fechaError = if (intentoGuardar) ReglasActividad.validarFecha(fechaSeleccionadaMillis) else null,
    )

    if (showDatePickerState.value) {
        val datePickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    // Bloquea fechas pasadas (HU 09)
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
                    fechaSeleccionadaMillis = datePickerState.selectedDateMillis
                    showDatePickerState.value = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerState.value = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (operacionUiState is OperacionUiState.Fallida) {
                Text(
                    text = operacionUiState.mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            
            FormularioActividad(
                uiState = uiState,
                onTituloChange = { titulo = it },
                onDescripcionChange = { descripcion = it },
                onProgresoChange = { progreso = it },
                onPrioridadChange = { prioridad = it },
                onFechaClick = { showDatePickerState.value = true },
                onGuardarClick = {
                    intentoGuardar = true
                    if (uiState.puedeGuardar) {
                        onActividadGuardada(
                            titulo.trim(), 
                            descripcion.trim(), 
                            progreso, 
                            prioridad,
                            fechaSeleccionadaMillis!!,
                        )
                    }
                },
                estaGuardando = operacionUiState is OperacionUiState.EnCurso,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioActividad(
    uiState: FormularioActividadUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onProgresoChange: (Int) -> Unit,
    onPrioridadChange: (Prioridad) -> Unit,
    onFechaClick: () -> Unit,
    onGuardarClick: () -> Unit,
    modifier: Modifier = Modifier,
    estaGuardando: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = uiState.titulo,
            onValueChange = onTituloChange,
            label = { Text("Título") },
            placeholder = { Text("Ej: Aprender Docker") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.tituloError != null,
            supportingText = {
                val error = uiState.tituloError
                if (error != null) {
                    Text(error)
                } else {
                    Text("${uiState.titulo.length}/80")
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = uiState.descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.descripcionError != null,
            supportingText = {
                val error = uiState.descripcionError
                if (error != null) {
                    Text(error)
                } else {
                    Text("${uiState.descripcion.length}/240")
                }
            },
            minLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )

        // Selección de Prioridad
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = uiState.prioridad.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Prioridad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                Prioridad.entries.forEach { p ->
                    DropdownMenuItem(
                        text = { Text(p.name) },
                        onClick = {
                            onPrioridadChange(p)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        // Selección de Fecha
        val fechaTexto = uiState.fechaSeleccionadaMillis?.let {
            java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date(it))
        } ?: "Seleccionar fecha límite"
        
        OutlinedCard(
            onClick = onFechaClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (uiState.fechaError != null) 
                    MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
            ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = fechaTexto, modifier = Modifier.weight(1f))
                Text(text = "📅", style = MaterialTheme.typography.headlineSmall)
            }
        }
        if (uiState.fechaError != null) {
            Text(
                text = uiState.fechaError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp),
            )
        }

        // Selector de Progreso (HU 11)
        Text(text = "Progreso Inicial: ${uiState.progreso}%", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = uiState.progreso.toFloat(),
            onValueChange = { onProgresoChange(it.toInt()) },
            valueRange = 0f..100f,
            steps = 10,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGuardarClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = (!uiState.intentoGuardar || uiState.puedeGuardar) && !estaGuardando,
        ) {
            if (estaGuardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text("Guardar Actividad")
            }
        }
    }
}

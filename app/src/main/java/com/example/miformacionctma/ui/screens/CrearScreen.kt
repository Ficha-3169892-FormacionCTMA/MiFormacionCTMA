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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import java.util.UUID

// 1. Estado UI del formulario
data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val prioridad: String = "MEDIA",
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val intentoGuardar: Boolean = false
) {
    val puedeGuardar: Boolean
        get() = titulo.trim().length in 3..80 && descripcion.length <= 240
}

// 2. Funciones puras de validación (Desacopladas de Compose)
fun validarTitulo(valor: String, mostrarVacio: Boolean): String? {
    val limpio = valor.trim()
    return when {
        limpio.isEmpty() && mostrarVacio -> "Escribe un título para la actividad"
        limpio.isNotEmpty() && limpio.length < 3 -> "Usa al menos 3 caracteres"
        limpio.length > 80 -> "Usa máximo 80 caracteres"
        else -> null
    }
}

fun validarDescripcion(valor: String): String? {
    return if (valor.length > 240) "Usa máximo 240 caracteres" else null
}

// 3. Pantalla contenedora de Estado (Stateful Screen)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearScreen(
    onGuardar: (ActividadFormativa) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado conservado ante rotaciones
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var prioridad by rememberSaveable { mutableStateOf("MEDIA") }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }

    // Evaluaciones dinámicas de validación
    val tituloError = validarTitulo(titulo, mostrarVacio = intentoGuardar)
    val descripcionError = validarDescripcion(descripcion)

    val uiState = FormularioActividadUiState(
        titulo = titulo,
        descripcion = descripcion,
        prioridad = prioridad,
        tituloError = tituloError,
        descripcionError = descripcionError,
        intentoGuardar = intentoGuardar
    )

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
            onTituloChange = { titulo = it.take(80) },
            onDescripcionChange = { descripcion = it.take(240) },
            onPrioridadChange = { prioridad = it },
            onGuardarClick = {
                intentoGuardar = true
                if (uiState.puedeGuardar) {
                    val nuevaActividad = ActividadFormativa(
                        id = System.currentTimeMillis(), // Genera un ID numérico único (Long)
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        prioridad = Prioridad.valueOf(prioridad), // Convierte el String al Enum Prioridad (o ajusta según tu Enum)
                        progreso = 0,
                        diasRestantes = 0 // Agrega el parámetro numérico que faltaba
                    )
                    onGuardar(nuevaActividad)
                }
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

// 4. Componente de presentación sin estado (Stateless Content)
@Composable
fun FormularioActividadContent(
    uiState: FormularioActividadUiState,
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onPrioridadChange: (String) -> Unit,
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

        // Selección de Prioridad
        Text(
            text = "Prioridad",
            style = MaterialTheme.typography.titleMedium
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("BAJA", "MEDIA", "ALTA").forEach { nivel ->
                FilterChip(
                    selected = uiState.prioridad == nivel,
                    onClick = { onPrioridadChange(nivel) },
                    label = { Text(nivel) }
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
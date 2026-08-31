package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearActividad(
    actividadId: Int? = null,
    initialTitulo: String = "",
    initialDescripcion: String = "",
    onVolver: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(initialTitulo) }
    var descripcion by rememberSaveable { mutableStateOf(initialDescripcion) }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    var guardando by rememberSaveable { mutableStateOf(false) }

    val tituloError = if (intentoGuardar && titulo.trim().isEmpty()) "El título es obligatorio" 
                     else if (titulo.length > 80) "Máximo 80 caracteres" else null
    val descripcionError = if (descripcion.length > 240) "Máximo 240 caracteres" else null
    val puedeGuardar = tituloError == null && titulo.trim().isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (actividadId == null) "Nueva Actividad" else "Editar Actividad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { if (it.length <= 80) titulo = it },
                label = { Text("Título *") },
                supportingText = { Text("${titulo.length}/80") },
                isError = tituloError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { if (it.length <= 240) descripcion = it },
                label = { Text("Descripción") },
                supportingText = { Text("${descripcion.length}/240") },
                isError = descripcionError != null,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    intentoGuardar = true
                    if (puedeGuardar && !guardando) {
                        guardando = true
                        onGuardar(titulo, descripcion)
                    }
                },
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (actividadId == null) "Guardar Actividad" else "Actualizar Actividad")
            }
        }
    }
}

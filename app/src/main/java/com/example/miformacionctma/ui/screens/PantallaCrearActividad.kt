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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

fun validarTitulo(valor: String, mostrarVacio: Boolean): String? {
    val limpio = valor.trim()
    return when {
        limpio.isEmpty() && mostrarVacio -> "Escribe un título para la actividad"
        limpio.isNotEmpty() && limpio.length < 3 -> "Usa al menos 3 caracteres"
        limpio.length > 80 -> "Usa máximo 80 caracteres"
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearActividad(
    onVolver: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var intentoGuardar by rememberSaveable { mutableStateOf(false) }
    var guardando by rememberSaveable { mutableStateOf(false) }

    val tituloError = validarTitulo(titulo, mostrarVacio = intentoGuardar)
    val descripcionError = if (descripcion.length > 240) "Usa máximo 240 caracteres" else null
    val puedeGuardar = tituloError == null && descripcionError == null && titulo.trim().isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Actividad", fontWeight = FontWeight.Bold) },
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
                onValueChange = { titulo = it },
                label = { Text("Título *") },
                supportingText = { Text(tituloError ?: "${titulo.length}/80") },
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
                supportingText = { Text(descripcionError ?: "${descripcion.length}/240") },
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
                Text(if (guardando) "Guardando..." else "Guardar Actividad")
            }
        }
    }
}
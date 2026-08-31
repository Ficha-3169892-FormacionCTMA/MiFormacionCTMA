package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.data.Actividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleActividad(
    actividad: Actividad?,
    onVolver: () -> Unit,
    onEditar: (Int) -> Unit,
    onToggleCompletada: (Int) -> Unit
) {
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
                },
                actions = {
                    if (actividad != null) {
                        IconButton(onClick = { onEditar(actividad.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar Actividad")
                        }
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
            if (actividad != null) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (actividad.descripcion.isBlank()) "Sin descripción" else actividad.descripcion,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LinearProgressIndicator(
                    progress = { actividad.progreso },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    text = "Progreso: ${(actividad.progreso * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { onToggleCompletada(actividad.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (actividad.completada) 
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary) 
                        else ButtonDefaults.buttonColors()
                ) {
                    Text(if (actividad.completada) "Marcar como Pendiente" else "Completar Actividad")
                }
            } else {
                Text("Actividad no encontrada")
            }
        }
    }
}

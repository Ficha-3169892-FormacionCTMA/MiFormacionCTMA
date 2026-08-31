package com.example.miformacionctma.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    actividades: List<Actividad>,
    mostrarFinalizadas: Boolean,
    onActividadClick: (Int) -> Unit,
    onCrearActividadClick: () -> Unit,
    onToggleFinalizadas: () -> Unit,
    onDeleteActividad: (Actividad) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA", fontWeight = FontWeight.Bold) },
                actions = {
                    FilterChip(
                        selected = mostrarFinalizadas,
                        onClick = onToggleFinalizadas,
                        label = { Text(if (mostrarFinalizadas) "Finalizadas" else "Pendientes") },
                        leadingIcon = if (mostrarFinalizadas) {
                            { Icon(Icons.Default.Done, contentDescription = null) }
                        } else {
                            { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearActividadClick) {
                Icon(Icons.Default.Add, contentDescription = "Crear Actividad")
            }
        }
    ) { paddingValores ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(
                items = actividades,
                key = { it.id }
            ) { actividad ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        if (it == SwipeToDismissBoxValue.EndToStart) {
                            onDeleteActividad(actividad)
                            true
                        } else {
                            false
                        }
                    }
                )

                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        val color by animateColorAsState(
                            when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                                else -> Color.Transparent
                            }, label = "dismiss_color"
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color.White
                            )
                        }
                    },
                    enableDismissFromStartToEnd = false
                ) {
                    TarjetaActividad(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad.id) }
                    )
                }
            }
        }
    }
}

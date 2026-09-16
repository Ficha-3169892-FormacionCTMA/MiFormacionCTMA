package com.example.miformacionctma.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.ui.states.OperacionUiState
import java.io.File
import java.util.UUID

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
    evidencias: List<Evidencia>,
    onVolverClick: () -> Unit,
    onGuardarProgreso: (Int) -> Unit = {},
    onAdjuntarEvidencia: (Uri) -> Unit = {},
    onEliminarEvidencia: (String) -> Unit = {},
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
                        evidencias = evidencias,
                        onGuardarProgreso = onGuardarProgreso,
                        onAdjuntarEvidencia = onAdjuntarEvidencia,
                        onEliminarEvidencia = onEliminarEvidencia,
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
    evidencias: List<Evidencia>,
    onGuardarProgreso: (Int) -> Unit,
    onAdjuntarEvidencia: (Uri) -> Unit,
    onEliminarEvidencia: (String) -> Unit,
    estaGuardando: Boolean,
) {
    val context = LocalContext.current
    var editandoProgreso by remember { mutableStateOf(value = false) }
    var nuevoProgreso by remember { mutableFloatStateOf(actividad.progreso.toFloat()) }

    // [HU 17] Captura con Cámara (FileProvider)
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            onAdjuntarEvidencia(tempPhotoUri!!)
        }
    }

    // [HU 17] Selección con Photo Picker
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let(onAdjuntarEvidencia)
    }

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
        
        // [HU 11] Control de Progreso Granular (Slider) - Barra Visual
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
            // [HU 13] Apertura de Enlaces de Evidencia
            OutlinedButton(
                onClick = { /* Lógica de apertura de link HU 13 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Evidencia")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Evidencias Fotográficas", style = MaterialTheme.typography.titleMedium)
        
        HorizontalDivider()

        if (evidencias.isEmpty()) {
            Text(text = "No hay evidencias adjuntas.", style = MaterialTheme.typography.bodySmall)
        } else {
            evidencias.forEach { evidencia ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AsyncImage(
                        model = evidencia.localUri,
                        contentDescription = "Vista previa evidencia",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Archivo: ${evidencia.id.take(8)}...", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Estado: ${evidencia.estado.name}", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = { onEliminarEvidencia(evidencia.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Botón Photo Picker
            OutlinedButton(
                onClick = {
                    pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galería")
            }

            // Botón Cámara
            Button(
                onClick = {
                    val file = File(context.filesDir, "evidencias").apply { mkdirs() }
                    val photoFile = File(file, "evidencia_${UUID.randomUUID()}.jpg")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                    tempPhotoUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cámara")
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

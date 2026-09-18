package com.example.miformacionctma.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaSyncState
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
    errorFeedback: String? = null,
    onClearError: () -> Unit = {},
    onVolverClick: () -> Unit,
    onEditClick: () -> Unit = {},
    onGuardarProgreso: (Int) -> Unit = {},
    onAgregarEvidencia: (Uri) -> Unit = {},
    onEliminarEvidencia: (String) -> Unit = {},
    onReintentarEvidencia: (String) -> Unit = {},
    operacionUiState: OperacionUiState = OperacionUiState.Inactiva,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    // CA-04: Mostrar error si el archivo es inválido o > 5MB
    LaunchedEffect(errorFeedback) {
        errorFeedback?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Actividad")
                    }
                }
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
                        onAgregarEvidencia = onAgregarEvidencia,
                        onEliminarEvidencia = onEliminarEvidencia,
                        onReintentarEvidencia = onReintentarEvidencia,
                        estaGuardando = operacionUiState is OperacionUiState.EnCurso,
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
                        modifier = Modifier.align(Alignment.Center),
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
    onAgregarEvidencia: (Uri) -> Unit,
    onEliminarEvidencia: (String) -> Unit,
    onReintentarEvidencia: (String) -> Unit,
    estaGuardando: Boolean,
) {
    val context = LocalContext.current
    var editandoProgreso by remember { mutableStateOf(value = false) }
    var nuevoProgreso by remember { mutableFloatStateOf(actividad.progreso.toFloat()) }

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            tempCameraUri?.let { onAgregarEvidencia(it) }
        } else {
            tempCameraUri?.let { uri ->
                try { context.contentResolver.delete(uri, null, null) } catch (_: Exception) {}
            }
        }
        tempCameraUri = null
    }

    val pickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onAgregarEvidencia(it) }
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

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Captura de Evidencia", style = MaterialTheme.typography.titleMedium)
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val file = File(context.filesDir, "evidencias/EVID_${UUID.randomUUID()}.jpg")
                    file.parentFile?.mkdirs()
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cámara")
            }
            OutlinedButton(
                onClick = {
                    pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Galería")
            }
        }

        if (evidencias.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.height(150.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(evidencias, key = { it.id }) { evidencia ->
                    CardEvidencia(
                        evidencia = evidencia,
                        onEliminar = { onEliminarEvidencia(evidencia.id) },
                        onReintentar = { onReintentarEvidencia(evidencia.id) },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Progreso Actual: ${actividad.progreso}%", style = MaterialTheme.typography.titleMedium)
        
        LinearProgressIndicator(
            progress = { actividad.progreso / 100f },
            modifier = Modifier.fillMaxWidth().height(8.dp),
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
                    Text(text = "Nuevo Porcentaje: ${nuevoProgreso.toInt()}%", style = MaterialTheme.typography.bodyLarge)
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
                        OutlinedButton(onClick = { editandoProgreso = false }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardEvidencia(
    evidencia: Evidencia,
    onEliminar: () -> Unit,
    onReintentar: () -> Unit
) {
    Card(modifier = Modifier.width(120.dp).fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = evidencia.localUri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier.fillMaxSize().padding(4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                when (evidencia.estado) {
                    EvidenciaSyncState.SUBIENDO -> CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    EvidenciaSyncState.SINCRONIZADA -> Icon(Icons.Default.CloudUpload, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    EvidenciaSyncState.FALLIDA -> IconButton(onClick = onReintentar, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.error)
                    }
                    else -> {}
                }
            }

            IconButton(
                onClick = onEliminar,
                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp).padding(4.dp)
            ) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.errorContainer)
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

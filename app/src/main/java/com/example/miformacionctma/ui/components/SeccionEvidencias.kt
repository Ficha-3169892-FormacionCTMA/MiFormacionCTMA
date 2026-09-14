package com.example.miformacionctma.ui.components

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.util.FileHelper

@Composable
fun SeccionEvidencias(
    onFotoLista: (Uri) -> Unit,
    onToggleNotificaciones: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // 1. Photo Picker (Seguro - Sin permisos de galería)
    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && FileHelper.validarEvidencia(context, uri)) {
            onFotoLista(uri)
        }
    }

    // 2. Cámara (Usando Content URI)
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { onFotoLista(it) }
        } else {
            // Manejar cancelación: Limpiar temporal si falló
            tempUri?.let { context.contentResolver.delete(it, null, null) }
        }
    }

    // 3. Permiso de Notificaciones (Contextual para Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> 
        onToggleNotificaciones(isGranted) 
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Evidencias", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { 
                pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) 
            }) {
                Text("Galería")
            }

            Button(onClick = {
                val uri = FileHelper.generarUriParaCamara(context)
                tempUri = uri
                cameraLauncher.launch(uri)
            }) {
                Text("Tomar Foto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Switch de recordatorios que pide permiso al activarse (HU14 Contextual)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Alertas de vencimiento", modifier = Modifier.weight(1f))
            Switch(
                checked = false, // Vincular a estado del ViewModel en integración real
                onCheckedChange = { active ->
                    if (active && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onToggleNotificaciones(active)
                    }
                }
            )
        }
    }
}

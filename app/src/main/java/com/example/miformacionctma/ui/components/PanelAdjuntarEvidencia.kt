package com.example.miformacionctma.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.miformacionctma.data.local.entities.EstadoSincronizacion
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import java.io.File

@Composable
fun PanelAdjuntarEvidencia(
    actividadId: Long,
    evidencias: List<EvidenciaEntity>,
    onUriSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var fotoUri by remember { mutableStateOf<Uri?>(null) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUriSelected(uri)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            fotoUri?.let { onUriSelected(it) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Evidencias Adjuntas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    try {
                        val tempDir = File(context.cacheDir, "evidencias")
                        if (!tempDir.exists()) tempDir.mkdirs()
                        val file = File(tempDir, "cam_${System.currentTimeMillis()}.jpg")
                        val authority = "${context.packageName}.fileprovider"
                        val uri = FileProvider.getUriForFile(context, authority, file)
                        fotoUri = uri
                        takePictureLauncher.launch(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Cámara", style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Galería", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (evidencias.isEmpty()) {
            Text(
                text = "No hay evidencias adjuntas para esta actividad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(evidencias) { evidencia ->
                    ItemEvidencia(evidencia = evidencia)
                }
            }
        }
    }
}

@Composable
fun ItemEvidencia(evidencia: EvidenciaEntity) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val uri = Uri.parse(evidencia.localUri)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, evidencia.mimeType)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Ver Evidencia con:"))
                } catch (e: Exception) {
                    Toast.makeText(context, "No se pudo abrir o visualizar el archivo", Toast.LENGTH_SHORT).show()
                }
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = "Archivo",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ID: ${evidencia.id} - ${evidencia.mimeType}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${(evidencia.sizeBytes / 1024)} KB (Presiona para abrir)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            EstadoIcon(estado = evidencia.estado)
        }
    }
}

@Composable
fun EstadoIcon(estado: EstadoSincronizacion) {
    when (estado) {
        EstadoSincronizacion.LOCAL -> Icon(
            Icons.Default.InsertDriveFile,
            contentDescription = "Local",
            tint = MaterialTheme.colorScheme.secondary
        )
        EstadoSincronizacion.SUBIENDO -> Icon(
            Icons.Default.CloudUpload,
            contentDescription = "Subiendo",
            tint = MaterialTheme.colorScheme.primary
        )
        EstadoSincronizacion.SINCRONIZADA -> Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Sincronizada",
            tint = MaterialTheme.colorScheme.primary
        )
        EstadoSincronizacion.FALLIDA -> Icon(
            Icons.Default.Error,
            contentDescription = "Fallida",
            tint = MaterialTheme.colorScheme.error
        )
    }
}

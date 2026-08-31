package com.example.miformacionctma.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.NivelPrioridad
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun TarjetaActividad(
    actividad: Actividad,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prioridad = actividad.obtenerPrioridad()

    val diferenciaMs = actividad.fechaLimite.time - Date().time
    val diasRestantes = TimeUnit.MILLISECONDS.toDays(diferenciaMs)

    val (colorEtiqueta, textoPrioridad) = when (prioridad) {
        NivelPrioridad.CRITICA -> Color(0xFFD32F2F) to " URGENTE ($diasRestantes días)"
        NivelPrioridad.ALTA -> Color(0xFFF57C00) to " Alta ($diasRestantes días)"
        NivelPrioridad.MEDIA -> Color(0xFF388E3C) to " Normal ($diasRestantes días)"
        NivelPrioridad.COMPLETADA -> Color(0xFF1976D2) to " Completada"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .background(colorEtiqueta.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = textoPrioridad,
                        color = colorEtiqueta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = actividad.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = { actividad.progreso },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp),
                    color = if (actividad.progreso >= 1.0f) Color(0xFF388E3C) else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(actividad.progreso * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

package com.example.miformacionctma.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad

@Composable
fun PrioridadChip(prioridad: Prioridad) {
    val colores = when (prioridad) {
        Prioridad.ALTA -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFFFDAD6), // Rojo suave (M3 Error Container)
            labelColor = Color(0xFF410002),
        )
        Prioridad.MEDIA -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFFFDDB3), // Naranja suave
            labelColor = Color(0xFF291800)
        )
        Prioridad.BAJA -> AssistChipDefaults.assistChipColors(
            containerColor = Color(0xFFD1E4FF), // Azul suave
            labelColor = Color(0xFF001D36)
        )
    }

    AssistChip(
        onClick = { },
        label = { Text(prioridad.name) },
        colors = colores,
        border = null
    )
}

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onActividadClick: (ActividadFormativa) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onActividadClick(actividad) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics {
                contentDescription = "Actividad: ${actividad.titulo}, Días restantes: ${actividad.diasRestantes}"
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                PrioridadChip(prioridad = actividad.prioridad)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = actividad.descripcion ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Progreso: ${actividad.progreso}%",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { actividad.progreso / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
package com.example.miformacionctma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

@Composable
fun EncabezadoFormacion(
    nombre: String,
    totalActividades: Int,
    completadas: Int,
    modifier: Modifier = Modifier
) {
    val porcentajeGeneral = if (totalActividades > 0) (completadas.toFloat() / totalActividades.toFloat()) else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "¡Hola, $nombre!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Resumen de tu progreso formativo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fila de métricas clave
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$completadas de $totalActividades completadas",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Avance general: ${(porcentajeGeneral * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                // Indicador circular de progreso global
                CircularProgressIndicator(
                    progress = { porcentajeGeneral },
                    modifier = Modifier.size(42.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                    strokeWidth = 5.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Header Dashboard Normal")
@Composable
fun PreviewEncabezadoDashboard() {
    MiFormacionCTMATheme {
        EncabezadoFormacion(
            nombre = "Aprendiz",
            totalActividades = 10,
            completadas = 4
        )
    }
}
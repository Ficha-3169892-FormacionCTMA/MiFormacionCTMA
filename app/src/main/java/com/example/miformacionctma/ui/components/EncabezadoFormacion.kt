package com.example.miformacionctma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

@Composable
fun EncabezadoFormacion(
    nombre: String,
    actividades: List<ActividadFormativa>,
    modifier: Modifier = Modifier
) {
    val totalActividades = actividades.size
    val completadas = actividades.count { it.progreso >= 100 }
    
    // Uso de ReglasActividad para obtener un promedio preciso (Criterio de Calidad)
    val promedioProgreso = ReglasActividad.promedioProgreso(actividades)
    val progresoNormalizado = (promedioProgreso / 100).toFloat()
    
    // Uso de ReglasActividad para identificar tareas críticas (Criterio de Valor)
    val urgentes = ReglasActividad.actividadesUrgentes(actividades)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
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
                }
                
                if (urgentes.isNotEmpty()) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${urgentes.size} urgentes")
                        }
                    }
                }
            }

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
                        text = "Avance general: ${promedioProgreso.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                // Indicador circular de progreso global
                CircularProgressIndicator(
                    progress = { progresoNormalizado },
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
            actividades = emptyList()
        )
    }
}

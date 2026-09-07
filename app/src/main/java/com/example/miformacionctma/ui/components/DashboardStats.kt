package com.example.miformacionctma.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ReglasActividad

@Composable
fun DashboardStats(actividades: List<ActividadFormativa>, modifier: Modifier = Modifier) {
    val totalActividades = actividades.size
    val promedio = ReglasActividad.promedioProgreso(actividades)
    val totalHoras = actividades.sumOf { it.horas }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumen de Formación",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("Actividades", totalActividades.toString())
                StatItem("Promedio", "${promedio.toInt()}%")
                StatItem("Total Horas", "${totalHoras}h")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.headlineSmall)
    }
}

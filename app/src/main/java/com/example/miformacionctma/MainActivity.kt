package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.*
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Datos de prueba (Mocks de dominio)
                    val listaActividades = listOf(
                        ActividadFormativa(1, "Configuración Git", "Semana 1", 100, 0, Prioridad.ALTA),
                        ActividadFormativa(2, "Fundamentos Kotlin", "Semana 2", 60, 2, Prioridad.ALTA),
                        ActividadFormativa(3, "Diseño de Interfaces", "Semana 3", 0, -1, Prioridad.MEDIA)
                    )

                    // Cálculos usando las reglas del dominio
                    val promedio = ReglasActividad.promedioProgreso(listaActividades)
                    val urgentes = ReglasActividad.actividadesUrgentes(listaActividades)

                    PantallaInicio(
                        nombre = "Aprendiz",
                        promedioTexto = "Promedio de Avance: %.1f%%".format(promedio),
                        urgentesTexto = "Actividades urgentes/pendientes: ${urgentes.size}"
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(
    nombre: String,
    promedioTexto: String,
    urgentesTexto: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Mi Formación CTMA",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Hola, $nombre")
        Text(text = "Resumen del núcleo de dominio (Semana 2):")

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta de resumen con datos procesados por ReglasActividad
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = promedioTexto,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = urgentesTexto,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Reemplaza 'MiFormacionCTMATheme' por el nombre de tu tema generado si es diferente
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaInicio(nombre = "Aprendiz")
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(nombre: String = "Aprendiz") {
    Column(
        modifier = Modifier
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
        Text(text = "Aquí organizarás actividades y evidencias.")

        Spacer(modifier = Modifier.height(24.dp))

        // Reto adicional de la guía: Agregar una tarjeta de compromiso
        TarjetaCompromiso()
    }
}

@Composable
fun TarjetaCompromiso() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Próximo compromiso:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Entrega de Evidencia Semana 1",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
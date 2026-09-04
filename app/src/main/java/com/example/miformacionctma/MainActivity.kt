@file:Suppress("SpellCheckingInspection")

package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.worker.NotificacionWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        programarNotificaciones()

        val listaActividadesFicticias = listOf(
            ActividadFormativa(
                id = 1,
                titulo = "Diseño de Caso de Uso y Diagrama de Clases",
                descripcion = "Modelado de diagramas UML para la fase de análisis.",
                progreso = 100,
                prioridad = Prioridad.ALTA,
                diasRestantes = 0,
                horas = 12,
                enlaceEvidencia = "https://example.com/cert1.pdf",
            ),
            ActividadFormativa(
                id = 2,
                titulo = "Construcción de API con FastAPI y SQLAlchemy",
                descripcion = "Desarrollo de endpoints de productos y autenticación.",
                progreso = 60,
                prioridad = Prioridad.ALTA,
                diasRestantes = 0, // HU14: menos de 24h
                horas = 20,
                enlaceEvidencia = "https://example.com/repo-api",
            ),
            ActividadFormativa(
                id = 3,
                titulo = "Interfaz Declarativa con Jetpack Compose",
                descripcion = "Construcción de componentes accesibles e interfaces adaptables.",
                progreso = 30,
                prioridad = Prioridad.MEDIA,
                diasRestantes = 5,
                horas = 15,
            ),
            ActividadFormativa(
                id = 4,
                titulo = "Configuración de Contenedores con Docker",
                descripcion = "Containerización y despliegue de microservicios.",
                progreso = 0,
                prioridad = Prioridad.BAJA,
                diasRestantes = 10,
                horas = 8,
            )
        )

        setContent {
            MiFormacionCTMATheme {
                PantallaActividades(
                    actividades = listaActividadesFicticias
                )
            }
        }
    }

    private fun programarNotificaciones() {
        val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "notificacion_vencimiento",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@file:Suppress("SpellCheckingInspection")

package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.repository.ActividadRepository
import com.example.miformacionctma.ui.screens.PantallaActividadesRoute
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import com.example.miformacionctma.worker.NotificacionWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    // Implementación rápida en memoria para cumplir con los requisitos de la Semana 7
    private val repository = object : ActividadRepository {
        private val _actividades = MutableStateFlow(
            listOf(
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
                    diasRestantes = 0,
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
        )

        override fun observarActividades(): Flow<List<ActividadFormativa>> = _actividades
        override fun buscar(query: String): Flow<List<ActividadFormativa>> = _actividades.map { lista ->
            lista.filter { it.titulo.contains(query, ignoreCase = true) }
        }
        override suspend fun guardar(actividad: ActividadFormativa) {
            _actividades.value = _actividades.value.map {
                if (it.id == actividad.id) actividad else it
            }
        }
        override suspend fun eliminar(id: String): Boolean = false
    }

    private val viewModel: ActividadesViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActividadesViewModel(repository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        programarNotificaciones()

        setContent {
            MiFormacionCTMATheme {
                PantallaActividadesRoute(viewModel = viewModel)
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
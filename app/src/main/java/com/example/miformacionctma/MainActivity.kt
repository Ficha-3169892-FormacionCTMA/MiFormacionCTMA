package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.miformacionctma.domain.*
import com.example.miformacionctma.ui.screens.ContenidoAdaptable
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
                    // Lista obligatoria de al menos 10 elementos para comprobar el LazyColumn y el Grid
                    val listaActividades = listOf(
                        ActividadFormativa(1, "Configuración Git", "Semana 1", 100, 0, Prioridad.ALTA),
                        ActividadFormativa(2, "Fundamentos Kotlin", "Semana 2", 100, 0, Prioridad.ALTA),
                        ActividadFormativa(3, "Diseño de Interfaces - Microprácticas", "Semana 3", 80, 0, Prioridad.MEDIA),
                        ActividadFormativa(4, "Laboratorio Integrador UI", "Semana 3", 40, 0, Prioridad.ALTA),
                        ActividadFormativa(5, "Reto de Adaptabilidad", "Semana 3", 0, 0, Prioridad.MEDIA),
                        ActividadFormativa(6, "Implementación de ViewModel", "Semana 4", 0, 0, Prioridad.ALTA),
                        ActividadFormativa(7, "Navegación con Jetpack Navigation", "Semana 4", 0, 0, Prioridad.MEDIA),
                        ActividadFormativa(8, "Evaluación de Accesibilidad", "Semana 4", 0, 0, Prioridad.BAJA),
                        ActividadFormativa(9, "Persistencia con Room (Local)", "Semana 5", 0, 0, Prioridad.ALTA),
                        ActividadFormativa(10, "Despliegue y Sustentación", "Semana 5", 0, 0, Prioridad.ALTA)
                    )

                    // Se delega el control de la UI al componente adaptable
                    ContenidoAdaptable(actividades = listaActividades)
                }
            }
        }
    }
}
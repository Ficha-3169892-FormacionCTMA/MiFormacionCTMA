package com.example.miformacionctma

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.navigation.MiFormacionNavHost
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiFormacionCTMATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado observable de la lista de actividades en memoria
                    val listaActividades = remember {
                        mutableStateListOf(
                            ActividadFormativa(
                                id = 1L,
                                titulo = "Evidencia 1: Mapa conceptual",
                                descripcion = "Elaborar mapa conceptual sobre arquitectura de software y requerimientos.",
                                prioridad = Prioridad.ALTA,
                                progreso = 100,
                                diasRestantes = 2,
                                fechaLimite = "20/05/2024"
                            ),
                            ActividadFormativa(
                                id = 2L,
                                titulo = "Evidencia 2: Taller Jetpack Compose",
                                descripcion = "Diseñar e implementar interfaz responsiva utilizando listas observables y estados.",
                                prioridad = Prioridad.MEDIA,
                                progreso = 50,
                                diasRestantes = 5,
                                fechaLimite = "25/05/2024"
                            ),
                            ActividadFormativa(
                                id = 3L,
                                titulo = "Evidencia 3: Formulario con validación",
                                descripcion = "Construir formulario controlado con reglas puras de validación y navegación.",
                                prioridad = Prioridad.ALTA,
                                progreso = 0,
                                diasRestantes = 7,
                                fechaLimite = "30/05/2024"
                            )
                        )
                    }

                    // Grafo de navegación principal
                    MiFormacionNavHost(
                        actividades = listaActividades,
                        onGuardarNuevaActividad = { nuevaActividad ->
                            // Uso de ReglasActividad para validar antes de agregar
                            val errores = ReglasActividad.validarActividad(nuevaActividad)
                            if (errores.isEmpty()) {
                                listaActividades.add(0, nuevaActividad)
                            } else {
                                Log.e("MainActivity", "Errores de validación: $errores")
                            }
                        },
                        onUpdateActividad = { actividadActualizada ->
                            val index = listaActividades.indexOfFirst { it.id == actividadActualizada.id }
                            if (index != -1) {
                                // También validamos al actualizar
                                if (ReglasActividad.validarActividad(actividadActualizada).isEmpty()) {
                                    listaActividades[index] = actividadActualizada
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

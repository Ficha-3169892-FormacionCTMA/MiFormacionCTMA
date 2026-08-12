package com.example.miformacionctma.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.components.TarjetaActividad

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaActividades(
    modifier: Modifier = Modifier
) {
    val actividadesEjemplo = listOf(
        // Las 4 iniciales
        ActividadFormativa(
            id = 1L,
            titulo = "Guía 05 - Persistencia de datos",
            descripcion = "Conectar la API REST con SQLite3 en FastAPI.",
            progreso = 75,
            diasRestantes = 2,
            estado = ReglasActividad.obtenerEstado(progreso = 75, diasRestantes = 2),
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 2L,
            titulo = "Pruebas de Desempeño",
            descripcion = "Crear pruebas unitarias y de integración.",
            progreso = 30,
            diasRestantes = 5,
            estado = ReglasActividad.obtenerEstado(progreso = 30, diasRestantes = 5),
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 3L,
            titulo = "Documentación del Proyecto",
            descripcion = "Actualizar manuales y diagramas UML.",
            progreso = 100,
            diasRestantes = 0,
            estado = ReglasActividad.obtenerEstado(progreso = 100, diasRestantes = 0),
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 4L,
            titulo = "Diseño de Interfaces Compose",
            descripcion = "Maquetar la pantalla de detalle de la actividad.",
            progreso = 0,
            diasRestantes = 7,
            estado = ReglasActividad.obtenerEstado(progreso = 0, diasRestantes = 7),
            prioridad = Prioridad.MEDIA
        ),
        // Las 6 nuevas actividades agregadas
        ActividadFormativa(
            id = 5L,
            titulo = "Modelo Entidad-Relación SORAKA",
            descripcion = "Diseñar el esquema de base de datos PostgreSQL en DBeaver.",
            progreso = 100,
            diasRestantes = -2,
            estado = ReglasActividad.obtenerEstado(progreso = 100, diasRestantes = -2),
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 6L,
            titulo = "Autenticación JWT en Backend",
            descripcion = "Implementar login y hashing de contraseñas con Passlib.",
            progreso = 90,
            diasRestantes = 1,
            estado = ReglasActividad.obtenerEstado(progreso = 90, diasRestantes = 1),
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 7L,
            titulo = "Configuración de Contenedores Docker",
            descripcion = "Crear Dockerfile y docker-compose para la API y la BD.",
            progreso = 40,
            diasRestantes = 4,
            estado = ReglasActividad.obtenerEstado(progreso = 40, diasRestantes = 4),
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 8L,
            titulo = "Control de Versiones y Ramas Git",
            descripcion = "Estructurar flujo de trabajo en GitHub con rama develop.",
            progreso = 100,
            diasRestantes = 0,
            estado = ReglasActividad.obtenerEstado(progreso = 100, diasRestantes = 0),
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 9L,
            titulo = "Navegación Jetpack Compose",
            descripcion = "Configurar NavHost y pasar parámetros entre pantallas.",
            progreso = 15,
            diasRestantes = 6,
            estado = ReglasActividad.obtenerEstado(progreso = 15, diasRestantes = 6),
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 10L,
            titulo = "Validación de Formulario Pydantic",
            descripcion = "Definir esquemas de entrada para productos y categorías.",
            progreso = 60,
            diasRestantes = 3,
            estado = ReglasActividad.obtenerEstado(progreso = 60, diasRestantes = 3),
            prioridad = Prioridad.ALTA
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Formación CTMA") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Actividades Formativas",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn {
                items(actividadesEjemplo, key = { it.id }) { actividad ->
                    TarjetaActividad(actividad = actividad)
                }
            }
        }
    }
}
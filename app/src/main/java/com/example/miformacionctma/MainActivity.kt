@file:Suppress("SpellCheckingInspection")

package com.example.miformacionctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.AppNavigation
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val listaActividadesFicticias = listOf(
            ActividadFormativa(
                id = 1,
                titulo = "Diseño de Caso de Uso y Diagrama de Clases",
                descripcion = "Modelado de diagramas UML para la fase de análisis.",
                progreso = 100,
                prioridad = Prioridad.ALTA,
                diasRestantes = 0,
                estado = EstadoActividad.COMPLETADA
            ),
            ActividadFormativa(
                id = 2,
                titulo = "Construcción de API con FastAPI y SQLAlchemy",
                descripcion = "Desarrollo de endpoints de productos y autenticación.",
                progreso = 60,
                prioridad = Prioridad.ALTA,
                diasRestantes = 3,
                estado = EstadoActividad.EN_PROGRESO
            ),
            ActividadFormativa(
                id = 3,
                titulo = "Interfaz Declarativa con Jetpack Compose",
                descripcion = "Construcción de componentes accesibles e interfaces adaptables.",
                progreso = 30,
                prioridad = Prioridad.MEDIA,
                diasRestantes = 5,
                estado = EstadoActividad.EN_PROGRESO
            ),
            ActividadFormativa(
                id = 4,
                titulo = "Configuración de Contenedores con Docker",
                descripcion = "Containerización y despliegue de microservicios.",
                progreso = 20,
                prioridad = Prioridad.BAJA,
                diasRestantes = -2, // Refleja que el plazo ya expiró
                estado = EstadoActividad.VENCIDA
            ),
            ActividadFormativa(
                id = 5,
                titulo = "Diseño de Base de Datos Relacional",
                descripcion = "Normalización y creación de modelos ER en PostgreSQL.",
                progreso = 85,
                prioridad = Prioridad.ALTA,
                diasRestantes = 2,
                estado = EstadoActividad.EN_PROGRESO
            ),
            ActividadFormativa(
                id = 6,
                titulo = "Implementación de Seguridad con JWT",
                descripcion = "Protección de rutas de API mediante tokens de autorización.",
                progreso = 45,
                prioridad = Prioridad.ALTA,
                diasRestantes = 4,
                estado = EstadoActividad.EN_PROGRESO
            ),
            ActividadFormativa(
                id = 7,
                titulo = "Pruebas Unitarias e Integración",
                descripcion = "Cobertura de pruebas automatizadas para servicios backend.",
                progreso = 20,
                prioridad = Prioridad.MEDIA,
                diasRestantes = 7,
                estado = EstadoActividad.PENDIENTE
            ),
            ActividadFormativa(
                id = 8,
                titulo = "Integración Continua con GitHub Actions",
                descripcion = "Configuración de flujos automatizados de compilación.",
                progreso = 10,
                prioridad = Prioridad.MEDIA,
                diasRestantes = 8,
                estado = EstadoActividad.PENDIENTE
            ),
            ActividadFormativa(
                id = 9,
                titulo = "Documentación Técnica de API con OpenAPI",
                descripcion = "Generación y estructuración de especificaciones Swagger.",
                progreso = 50,
                prioridad = Prioridad.BAJA,
                diasRestantes = 12,
                estado = EstadoActividad.EN_PROGRESO
            ),
            ActividadFormativa(
                id = 10,
                titulo = "Arquitectura en Capas con Spring Boot",
                descripcion = "Estructuración modular de la lógica de negocio y persistencia.",
                progreso = 15,
                prioridad = Prioridad.ALTA,
                diasRestantes = 15,
                estado = EstadoActividad.PENDIENTE
            )
        )

        setContent {
            MiFormacionCTMATheme {
                // Pasamos la lista inicial al contenedor de navegación que ahora gestiona el estado
                AppNavigation(actividadesIniciales = listaActividadesFicticias)
            }
        }
    }
}
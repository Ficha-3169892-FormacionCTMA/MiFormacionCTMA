package com.example.miformacionctma.data

import java.util.Date
import java.util.concurrent.TimeUnit

object MockData {
    private fun obtenerFechaFutura(dias: Long): Date {
        return Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(dias))
    }

    fun obtenerActividades(): List<Actividad> {
        return listOf(
            Actividad(
                id = 1,
                titulo = "Taller de Jetpack Compose",
                descripcion = "Implementar navegación, MVVM y manejo de estado con Kotlin.",
                fechaLimite = obtenerFechaFutura(1), // URGENTE
                progreso = 0.4f
            ),
            Actividad(
                id = 2,
                titulo = "Modelo Entidad Relación",
                descripcion = "Normalización de tablas y diseño conceptual de base de datos.",
                fechaLimite = obtenerFechaFutura(4), // ALTA
                progreso = 0.75f
            ),
            Actividad(
                id = 3,
                titulo = "Documentación de Proyecto",
                descripcion = "Redacción de casos de uso, diagrama de clases y requerimientos.",
                fechaLimite = obtenerFechaFutura(10), // MEDIA
                progreso = 0.2f
            ),
            Actividad(
                id = 4,
                titulo = "Desarrollo de API RESTful",
                descripcion = "Creación de endpoints para gestión de usuarios y autenticación.",
                fechaLimite = obtenerFechaFutura(2), // URGENTE
                progreso = 0.85f
            ),
            Actividad(
                id = 5,
                titulo = "Contenedorización con Docker",
                descripcion = "Configuración de Dockerfile y docker-compose para despliegue.",
                fechaLimite = obtenerFechaFutura(6), // MEDIA
                progreso = 0.1f
            ),
            Actividad(
                id = 6,
                titulo = "Pruebas Unitarias",
                descripcion = "Ejecución de tests automatizados para validar lógica del ViewModel.",
                fechaLimite = obtenerFechaFutura(3), // ALTA
                progreso = 0.5f
            ),
            Actividad(
                id = 7,
                titulo = "Diseño de Interfaz UX/UI",
                descripcion = "Bocetos de alambre y prototipo interactivo en Figma.",
                fechaLimite = obtenerFechaFutura(12), // MEDIA
                progreso = 1.0f,
                completada = true
            ),
            Actividad(
                id = 8,
                titulo = "Normalización y Consultas SQL",
                descripcion = "Creación de vistas, triggers y procedimientos almacenados.",
                fechaLimite = obtenerFechaFutura(1), // URGENTE
                progreso = 0.6f
            ),
            Actividad(
                id = 9,
                titulo = "Autenticación JWT",
                descripcion = "Implementación de roles, permisos y cifrado de tokens de acceso.",
                fechaLimite = obtenerFechaFutura(5), // ALTA
                progreso = 0.3f
            ),
            Actividad(
                id = 10,
                titulo = "Humillar a thomasin",
                descripcion = "prr prr petaquin.",
                fechaLimite = obtenerFechaFutura(8), // MEDIA
                progreso = 0.9f
            )
        )
    }
}
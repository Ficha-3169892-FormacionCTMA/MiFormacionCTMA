package com.example.miformacionctma.domain

object MockData {
    val listaActividades = listOf(
        ActividadFormativa(
            id = 1L,
            titulo = "Guía de Jetpack Compose",
            descripcion = "12 Ago 2026",
            progreso = 80,
            diasRestantes = 3,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 2L,
            titulo = "Modelado de Base de Datos",
            descripcion = "05 Jul 2026",
            progreso = 100,
            diasRestantes = 0,
            prioridad = Prioridad.MEDIA
        ),
        ActividadFormativa(
            id = 3L,
            titulo = "Pruebas Unitarias Kotlin",
            descripcion = "18 Ago 2026",
            progreso = 0,
            diasRestantes = 5,
            prioridad = Prioridad.BAJA
        ),
        ActividadFormativa(
            id = 4L,
            titulo = "Configuración de Docker",
            descripcion = "20 May 2026",
            progreso = 100,
            diasRestantes = 0,
            prioridad = Prioridad.ALTA
        ),
        ActividadFormativa(
            id = 5L,
            titulo = "Diseño de API REST",
            descripcion = "25 Ago 2026",
            progreso = 30,
            diasRestantes = 8,
            prioridad = Prioridad.MEDIA
        )
    )
}
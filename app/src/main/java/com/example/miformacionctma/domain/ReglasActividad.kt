@file:Suppress("unused", "SpellCheckingInspection")

package com.example.miformacionctma.domain

object ReglasActividad {

    // 1. Validar actividad (devuelve TODOS los errores encontrados)
    @Suppress("unused")
    fun validarActividad(titulo: String, progreso: Int): List<String> = buildList {
        if (titulo.isBlank()) {
            add("El título no puede estar vacío.")
        }
        if (progreso !in 0..100) {
            add("El progreso debe estar entre 0 y 100.")
        }
    }

    // 2. Determinar estado de la actividad
    fun obtenerEstado(progreso: Int, diasRestantes: Int): EstadoActividad {
        return when {
            progreso == 100 -> EstadoActividad.COMPLETADA
            diasRestantes < 0 -> EstadoActividad.VENCIDA
            progreso == 0 -> EstadoActividad.PENDIENTE
            else -> EstadoActividad.EN_PROGRESO
        }
    }
}
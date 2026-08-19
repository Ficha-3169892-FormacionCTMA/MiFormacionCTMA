package com.example.miformacionctma.data

import java.util.Date
import java.util.concurrent.TimeUnit

enum class NivelPrioridad {
    CRITICA, // Menos de 2 días
    ALTA,    // Entre 2 y 5 días
    MEDIA,   // Más de 5 días
    COMPLETADA
}

data class Actividad(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fechaLimite: Date,
    val progreso: Float = 0.0f, // Valor entre 0.0f (0%) y 1.0f (100%)
    val completada: Boolean = false
) {
    fun obtenerPrioridad(): NivelPrioridad {
        if (completada || progreso >= 1.0f) return NivelPrioridad.COMPLETADA

        val diferenciaMs = fechaLimite.time - Date().time
        val diasFaltantes = TimeUnit.MILLISECONDS.toDays(diferenciaMs)

        return when {
            diasFaltantes <= 2 -> NivelPrioridad.CRITICA
            diasFaltantes <= 5 -> NivelPrioridad.ALTA
            else -> NivelPrioridad.MEDIA
        }
    }
}
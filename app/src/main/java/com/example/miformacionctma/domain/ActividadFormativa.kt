package com.example.miformacionctma.domain

import kotlinx.serialization.Serializable

/**
 * Modelo de dominio puro.
 * Representa la actividad formativa sin acoplamiento a la base de datos o red.
 */
@Serializable
data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String? = null,
    val progreso: Int,
    val diasRestantes: Int,
    val estado: EstadoActividad,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val horas: Int = 10,
    val enlaceEvidencia: String? = null,
)

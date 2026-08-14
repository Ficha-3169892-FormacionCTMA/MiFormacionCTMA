package com.example.miformacionctma.domain

data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String? = null,
    val progreso: Int,
    val diasRestantes: Int,
    val estado: EstadoActividad,
    val prioridad: Prioridad = Prioridad.MEDIA
)
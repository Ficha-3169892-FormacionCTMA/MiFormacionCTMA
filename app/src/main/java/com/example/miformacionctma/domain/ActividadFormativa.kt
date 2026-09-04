package com.example.miformacionctma.domain

data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad,
    val horas: Int = 10,
    val enlaceEvidencia: String? = null,
)

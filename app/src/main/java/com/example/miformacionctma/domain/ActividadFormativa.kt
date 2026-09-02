package com.example.miformacionctma.domain

data class ActividadFormativa(
    val id: Long,
    val titulo: String,
    val descripcion: String,
    val progreso: Int,
    val diasRestantes: Int,
    val prioridad: Prioridad,
    val fechaLimite: String? = null,
    val enlaceEvidencia: String? = null
)
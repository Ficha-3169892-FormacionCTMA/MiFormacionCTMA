package com.example.miformacionctma.domain

import kotlinx.serialization.Serializable

@Serializable
enum class EstadoActividad {
    PENDIENTE,
    EN_PROGRESO,
    COMPLETADA,
    VENCIDA
}

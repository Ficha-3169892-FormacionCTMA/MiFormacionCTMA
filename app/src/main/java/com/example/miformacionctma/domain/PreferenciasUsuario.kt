package com.example.miformacionctma.domain

/**
 * [HU 07] Filtros Persistentes & [HU 14] Notificaciones
 */
data class PreferenciasUsuario(
    val filtroPrioridad: Prioridad? = null,
    val ordenadoPorVencimiento: Boolean = false,
    val modoCuadricula: Boolean = false,
    val notificacionesActivas: Boolean = false
)

package com.example.miformacionctma.domain

data class PreferenciasUsuario(
    val filtroPrioridad: Prioridad? = null,
    val ordenadoPorVencimiento: Boolean = false,
    val modoCuadricula: Boolean = false
)

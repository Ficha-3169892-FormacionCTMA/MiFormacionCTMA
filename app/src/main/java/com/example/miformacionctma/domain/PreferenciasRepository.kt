package com.example.miformacionctma.domain

import kotlinx.coroutines.flow.Flow

interface PreferenciasRepository {
    val preferencias: Flow<PreferenciasUsuario>
    suspend fun guardarFiltroPrioridad(prioridad: Prioridad?)
    suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean)
    suspend fun guardarModoCuadricula(activo: Boolean)
}

package com.example.miformacionctma.repository

import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.Flow

interface ActividadRepository {
    fun observarActividades(): Flow<List<ActividadFormativa>>
    fun buscar(query: String): Flow<List<ActividadFormativa>>
    suspend fun guardar(actividad: ActividadFormativa)
    suspend fun eliminar(id: String): Boolean
}
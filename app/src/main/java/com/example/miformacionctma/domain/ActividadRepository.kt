package com.example.miformacionctma.domain

import kotlinx.coroutines.flow.Flow

interface ActividadRepository {
    fun observarTodos(): Flow<List<ActividadFormativa>>
    fun observarPorId(id: Long): Flow<ActividadFormativa?>
    fun buscar(texto: String): Flow<List<ActividadFormativa>>
    suspend fun guardar(actividad: ActividadFormativa)
    suspend fun eliminar(id: Long): Boolean
}

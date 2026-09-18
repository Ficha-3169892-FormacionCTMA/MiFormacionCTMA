package com.example.miformacionctma.domain

import kotlinx.coroutines.flow.Flow

interface EvidenciaRepository {
    fun observarPorActividad(actividadId: Long): Flow<List<Evidencia>>
    suspend fun guardarLocal(evidencia: Evidencia)
    suspend fun subir(evidenciaId: String)
    suspend fun eliminar(evidenciaId: String)
}

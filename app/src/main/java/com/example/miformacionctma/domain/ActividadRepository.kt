package com.example.miformacionctma.domain

import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

interface ActividadRepository {
    fun observarTodos(): Flow<List<ActividadFormativa>>
    fun observarPorId(id: Long): Flow<ActividadFormativa?>
    fun buscar(texto: String): Flow<List<ActividadFormativa>>
    suspend fun guardar(actividad: ActividadFormativa): Long
    suspend fun eliminar(id: Long): Boolean
    
    // Semana 9: Evidencias
    fun observarEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>>
    suspend fun guardarEvidencia(evidencia: EvidenciaEntity)
}

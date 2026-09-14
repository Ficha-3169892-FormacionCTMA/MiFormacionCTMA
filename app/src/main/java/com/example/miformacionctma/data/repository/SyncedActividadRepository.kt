package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import com.example.miformacionctma.data.remote.dto.ActividadDto
import com.example.miformacionctma.data.remote.dto.toDto
import com.example.miformacionctma.data.remote.dto.toDomain
import com.example.miformacionctma.data.supabaseClient
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Repositorio híbrido (Offline-First) que coordina Room y Supabase.
 * Extendido para la Semana 9 con soporte para evidencias y estados.
 */
class SyncedActividadRepository(
    private val dao: ActividadDao,
    private val evidenciaDao: EvidenciaDao,
    private val scope: CoroutineScope,
) : ActividadRepository {

    override fun observarTodos(): Flow<List<ActividadFormativa>> =
        dao.observarTodos().map { entities -> entities.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<ActividadFormativa?> =
        dao.observarPorId(id).map { it?.toDomain() }

    override fun buscar(texto: String): Flow<List<ActividadFormativa>> =
        dao.buscar(texto).map { entities -> entities.map { it.toDomain() } }

    suspend fun sincronizarDesdeNube() {
        withContext(Dispatchers.IO) {
            try {
                val remoteDtos = supabaseClient.from("actividades")
                    .select()
                    .decodeList<ActividadDto>()
                
                remoteDtos.forEach { dto ->
                    dao.insertar(dto.toDomain().toEntity(competenciaId = 1L))
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncedRepo", "❌ FALLO RECUPERACIÓN: ${e.message}")
            }
        }
    }

    override suspend fun guardar(actividad: ActividadFormativa): Long {
        return withContext(Dispatchers.IO) {
            val generatedId = dao.insertar(actividad.toEntity(competenciaId = 1L))
            val actividadFinal = actividad.copy(id = generatedId)

            scope.launch {
                try {
                    val dto = actividadFinal.toDto()
                    supabaseClient.from("actividades").upsert(dto)
                } catch (e: Exception) {
                    android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: ${e.message}")
                }
            }
            generatedId
        }
    }

    override suspend fun eliminar(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val result = dao.eliminarPorId(id) == 1
            if (result) {
                scope.launch {
                    try {
                        supabaseClient.from("actividades").delete {
                            filter { eq("id", id) }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: ${e.message}")
                    }
                }
            }
            result
        }
    }

    // =========================================================================
    // SEMANA 9: GESTIÓN DE EVIDENCIAS
    // =========================================================================

    override fun observarEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> =
        evidenciaDao.observarEvidenciasPorActividad(actividadId)

    override suspend fun guardarEvidencia(evidencia: EvidenciaEntity) {
        withContext(Dispatchers.IO) {
            // 1. Registro local (SSOT)
            val generatedId = evidenciaDao.insertar(evidencia.copy(estado = "LOCAL"))
            
            // 2. Intento de subida (Manteniendo copia local si falla)
            scope.launch {
                try {
                    evidenciaDao.actualizarEstado(generatedId, "SUBIENDO")
                    
                    // Simulación de subida (Semana 10 implementará Storage real)
                    // val success = storage.subirArchivo(evidencia.localUri)
                    val success = true 
                    
                    if (success) {
                        evidenciaDao.actualizarEstado(generatedId, "SINCRONIZADA")
                    } else {
                        evidenciaDao.actualizarEstado(generatedId, "FALLIDA")
                    }
                } catch (e: Exception) {
                    evidenciaDao.actualizarEstado(generatedId, "FALLIDA")
                    if (e is CancellationException) throw e
                }
            }
        }
    }
}

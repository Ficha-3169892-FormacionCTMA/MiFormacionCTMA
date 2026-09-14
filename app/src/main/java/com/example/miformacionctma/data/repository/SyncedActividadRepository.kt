package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.remote.dto.ActividadDto
import com.example.miformacionctma.data.remote.dto.toDto
import com.example.miformacionctma.data.remote.dto.toDomain
import com.example.miformacionctma.data.supabaseClient
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repositorio híbrido (Offline-First) que coordina Room y Supabase.
 * Implementa resiliencia y separación de modelos (Semana 8).
 */
class SyncedActividadRepository(
    private val dao: ActividadDao,
    private val scope: CoroutineScope,
) : ActividadRepository {

    override fun observarTodos(): Flow<List<ActividadFormativa>> =
        dao.observarTodos().map { entities -> entities.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<ActividadFormativa?> =
        dao.observarPorId(id).map { it?.toDomain() }

    override fun buscar(texto: String): Flow<List<ActividadFormativa>> =
        dao.buscar(texto).map { entities -> entities.map { it.toDomain() } }

    /**
     * Descarga todas las actividades de la nube y las guarda en Room.
     * Esto permite recuperar datos al reinstalar la app o cambiar de dispositivo.
     */
    suspend fun sincronizarDesdeNube() {
        withContext(Dispatchers.IO) {
            try {
                val remoteDtos = supabaseClient.from("actividades")
                    .select()
                    .decodeList<ActividadDto>()
                
                remoteDtos.forEach { dto ->
                    dao.insertar(dto.toDomain().toEntity(competenciaId = 1L))
                }
                android.util.Log.d("SyncedRepo", "📥 RECUPERACIÓN: ${remoteDtos.size} actividades descargadas de Supabase.")
            } catch (e: Exception) {
                android.util.Log.e("SyncedRepo", "❌ FALLO RECUPERACIÓN: ${e.message}")
            }
        }
    }

    override suspend fun guardar(actividad: ActividadFormativa) {
        withContext(Dispatchers.IO) {
            // 1. Persistencia Local (SSOT)
            val generatedId = dao.insertar(actividad.toEntity(competenciaId = 1L))
            val actividadFinal = actividad.copy(id = generatedId)

            // 2. Sincronización Remota Resiliente
            scope.launch {
                try {
                    val dto = actividadFinal.toDto()
                    supabaseClient.from("actividades").upsert(dto)
                    android.util.Log.d("SyncedRepo", "✅ SINCRONIZADO: '${actividadFinal.titulo}' subida a Supabase.")
                } catch (e: Exception) {
                    android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: Fallo de sincronización remota: ${e.message}")
                }
            }
        }
    }

    override suspend fun eliminar(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val result = dao.eliminarPorId(id) == 1
            if (result) {
                scope.launch {
                    try {
                        supabaseClient.from("actividades").delete {
                            filter {
                                eq("id", id)
                            }
                        }
                        android.util.Log.d("SyncedRepo", "🗑️ ELIMINADO: Actividad #$id borrada de Supabase.")
                    } catch (e: Exception) {
                        android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: No se pudo eliminar en la nube: ${e.message}")
                    }
                }
            }
            result
        }
    }
}

package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.remote.dto.ActividadDto
import com.example.miformacionctma.data.remote.dto.toDto
import com.example.miformacionctma.data.remote.dto.toDomain
import com.example.miformacionctma.data.supabaseClient
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repositorio híbrido (Offline-First) que coordina Room y Supabase.
 * Implementa resiliencia y separación de modelos.
 */
class SyncedActividadRepository(
    private val dao: ActividadDao,
    private val scope: CoroutineScope,
    private val supabase: SupabaseClient = supabaseClient,
) : ActividadRepository {

    companion object {
        private const val DEFAULT_COMPETENCIA_ID = 1L
        private const val TABLA_ACTIVIDADES = "actividades"
    }

    override fun observarTodos(): Flow<List<ActividadFormativa>> =
        dao.observarTodos().map { entities -> entities.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<ActividadFormativa?> =
        dao.observarPorId(id).map { it?.toDomain() }

    override fun buscar(texto: String): Flow<List<ActividadFormativa>> =
        dao.buscar(texto).map { entities -> entities.map { it.toDomain() } }

    /**
     * Descarga todas las actividades de la nube y las guarda en Room.
     * Preserva metadatos locales como evidencias.
     */
    suspend fun sincronizarDesdeNube() {
        withContext(Dispatchers.IO) {
            try {
                val remoteDtos = supabase.from(TABLA_ACTIVIDADES)
                    .select()
                    .decodeList<ActividadDto>()
                
                remoteDtos.forEach { dto ->
                    val localActual = dao.obtenerPorId(dto.id)
                    val entidadNueva = dto.toDomain().toEntity(competenciaId = DEFAULT_COMPETENCIA_ID)
                    
                    // PRESERVAR: Mantenemos el enlace de evidencia local si existe
                    val entidadFinal = if (localActual != null) {
                        entidadNueva.copy(enlaceEvidencia = localActual.enlaceEvidencia)
                    } else {
                        entidadNueva
                    }
                    
                    dao.insertar(entidadFinal)
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncedRepo", "❌ FALLO RECUPERACIÓN: ${e.message}")
            }
        }
    }

    override suspend fun guardar(actividad: ActividadFormativa) {
        withContext(Dispatchers.IO) {
            // 1. Persistencia Local (SSOT)
            // Preservar enlace de evidencia si es una actualización
            val localActual = if (actividad.id != 0L) dao.obtenerPorId(actividad.id) else null
            val enlace = actividad.enlaceEvidencia ?: localActual?.enlaceEvidencia
            
            val entidadParaGuardar = actividad.toEntity(competenciaId = DEFAULT_COMPETENCIA_ID)
                .copy(enlaceEvidencia = enlace)

            val generatedId = dao.insertar(entidadParaGuardar)
            
            // IMPORTANTE: Solo usamos el generatedId si el original era 0 para evitar duplicados por re-mapeo de IDs
            val finalId = if (actividad.id == 0L) generatedId else actividad.id
            val actividadFinal = actividad.copy(id = finalId, enlaceEvidencia = enlace)

            // 2. Sincronización Remota Resiliente
            scope.launch {
                try {
                    val dto = actividadFinal.toDto()
                    supabase.from(TABLA_ACTIVIDADES).upsert(dto)
                } catch (e: Exception) {
                    android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: Fallo de sincronización remota: ${e.message}")
                }
            }
        }
    }

    override suspend fun actualizarProgreso(id: Long, progreso: Int) {
        withContext(Dispatchers.IO) {
            try {
                // Actualización ATÓMICA: Solo cambia progreso y estado de completitud. No toca evidencias.
                dao.actualizarProgreso(id, progreso, progreso >= 100)
                
                // Sincronizar con la nube
                scope.launch {
                    try {
                        // Enviamos solo los campos necesarios para evitar sobreescribir todo en la nube
                        supabase.from(TABLA_ACTIVIDADES).upsert(mapOf(
                            "id" to id,
                            "progreso" to progreso
                        ))
                    } catch (e: Exception) {
                        android.util.Log.e("SyncedRepo", "❌ ERROR NUBE (Avance): ${e.message}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SyncedRepo", "❌ ERROR LOCAL (Avance): ${e.message}")
            }
        }
    }

    override suspend fun eliminar(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val result = dao.eliminarPorId(id) == 1
            if (result) {
                scope.launch {
                    try {
                        supabase.from(TABLA_ACTIVIDADES).delete {
                            filter {
                                eq("id", id)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("SyncedRepo", "❌ ERROR NUBE: No se pudo eliminar en la nube: ${e.message}")
                    }
                }
            }
            result
        }
    }
}

package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
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

/**
 * Repositorio híbrido que utiliza Room para funcionamiento Offline
 * y Supabase para persistencia en la nube.
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

    override suspend fun guardar(actividad: ActividadFormativa) {
        withContext(Dispatchers.IO) {
            // 1. Guardado local (Room) - Obtenemos el ID generado
            val generatedId = dao.insertar(actividad.toEntity(competenciaId = 1L))
            
            // Creamos una copia de la actividad con el ID real para la nube
            val actividadConId = actividad.copy(id = generatedId)

            // 2. Sincronización en la nube (Supabase) - Operación de fondo
            scope.launch {
                try {
                    supabaseClient.from("actividades").upsert(actividadConId)
                } catch (e: Exception) {
                    android.util.Log.e("SyncedRepo", "Error al sincronizar con Supabase", e)
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
                    } catch (e: Exception) {
                        android.util.Log.e("SyncedRepo", "Error al eliminar en Supabase", e)
                    }
                }
            }
            result
        }
    }
}

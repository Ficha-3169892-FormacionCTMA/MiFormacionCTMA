package com.example.miformacionctma.data.repository

import android.content.Context
import android.net.Uri
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.toDomain
import com.example.miformacionctma.data.local.entities.toEntity
import com.example.miformacionctma.data.supabaseClient
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.EvidenciaSyncState
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class SyncedEvidenciaRepository(
    private val context: Context,
    private val dao: EvidenciaDao
) : EvidenciaRepository {

    override fun observarPorActividad(actividadId: Long): Flow<List<Evidencia>> =
        dao.observarPorActividad(actividadId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun guardarLocal(actividadId: Long, uri: Uri): Result<Evidencia> = withContext(Dispatchers.IO) {
        runCatching {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri) ?: "image/jpeg"
            
            // 1. Crear archivo local en directorio restringido
            val evidenciasDir = File(context.filesDir, "evidencias").apply { mkdirs() }
            val fileName = "evidencia_${UUID.randomUUID()}.jpg"
            val localFile = File(evidenciasDir, fileName)

            resolver.openInputStream(uri)?.use { input ->
                localFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: error("No se pudo leer la URI")

            val evidencia = Evidencia(
                id = UUID.randomUUID().toString(),
                actividadId = actividadId,
                localUri = localFile.absolutePath,
                mimeType = mimeType,
                sizeBytes = localFile.length(),
                estado = EvidenciaSyncState.LOCAL,
                creadaEnEpochMillis = System.currentTimeMillis()
            )

            // 2. Persistir en Room
            dao.insertar(evidencia.toEntity())
            evidencia
        }
    }

    override suspend fun sincronizar(evidenciaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val entity = dao.obtenerPorId(evidenciaId) ?: error("Evidencia no encontrada")
            val localFile = File(entity.localUri)
            if (!localFile.exists()) error("Archivo local no existe")

            dao.actualizarEstado(evidenciaId, EvidenciaSyncState.SUBIENDO.name)

            try {
                val bucket = supabaseClient.storage.from("evidencias")
                val remotePath = "${entity.actividadId}/${entity.id}.jpg"
                
                bucket.upload(remotePath, localFile.readBytes())
                
                dao.actualizarEstado(evidenciaId, EvidenciaSyncState.SINCRONIZADA.name)
            } catch (e: Exception) {
                dao.actualizarEstado(evidenciaId, EvidenciaSyncState.FALLIDA.name)
                throw e
            }
        }
    }

    override suspend fun eliminar(evidenciaId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val entity = dao.obtenerPorId(evidenciaId) ?: return@runCatching
            
            // 1. Eliminar archivo local
            val localFile = File(entity.localUri)
            if (localFile.exists()) localFile.delete()

            // 2. Eliminar de Room
            dao.eliminarPorId(evidenciaId)

            // 3. Intentar eliminar de la nube (best effort)
            try {
                val bucket = supabaseClient.storage.from("evidencias")
                val remotePath = "${entity.actividadId}/${entity.id}.jpg"
                bucket.delete(remotePath)
            } catch (e: Exception) {
                android.util.Log.w("EvidenciaRepo", "No se pudo eliminar de la nube: ${e.message}")
            }
        }
    }
}

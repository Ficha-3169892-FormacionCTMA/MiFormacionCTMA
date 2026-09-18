package com.example.miformacionctma.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import com.example.miformacionctma.data.remote.api.EvidenciaApi
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.EvidenciaSyncState
import java.io.InputStream
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

class DefaultEvidenciaRepository(
    private val dao: EvidenciaDao,
    private val api: EvidenciaApi,
    private val contentResolver: ContentResolver
) : EvidenciaRepository {

    override fun observarPorActividad(actividadId: Long): Flow<List<Evidencia>> =
        dao.observarPorActividad(actividadId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun guardarLocal(evidencia: Evidencia) {
        withContext(Dispatchers.IO) {
            dao.insertar(evidencia.toEntity())
        }
    }

    override suspend fun subir(evidenciaId: String) {
        val entity = dao.obtenerPorId(evidenciaId) ?: return
        if (entity.estado == EvidenciaSyncState.SINCRONIZADA.name) return

        try {
            // 1. Cambiar a SUBIENDO
            dao.actualizarEstado(evidenciaId, EvidenciaSyncState.SUBIENDO.name)

            val uri = Uri.parse(entity.localUri)
            val inputStream = contentResolver.openInputStream(uri) 
                ?: throw Exception("No se pudo abrir el archivo")

            val requestBody = object : RequestBody() {
                override fun contentType() = entity.mimeType.toMediaTypeOrNull()
                override fun contentLength() = entity.sizeBytes
                override fun writeTo(sink: BufferedSink) {
                    inputStream.source().use { sink.writeAll(it) }
                }
            }

            val part = MultipartBody.Part.createFormData("imagen", "evidencia_${evidenciaId}", requestBody)
            
            // 2. Ejecutar petición con Retrofit e Idempotency-Key
            val response = api.subirEvidencia(
                actividadId = entity.actividadId,
                imagen = part,
                idempotencyKey = evidenciaId
            )

            if (response.isSuccessful) {
                dao.actualizarEstado(evidenciaId, EvidenciaSyncState.SINCRONIZADA.name)
            } else {
                dao.actualizarEstado(evidenciaId, EvidenciaSyncState.FALLIDA.name)
            }
        } catch (e: CancellationException) {
            // 4. Regla de Cancelación
            dao.actualizarEstado(evidenciaId, EvidenciaSyncState.LOCAL.name)
            throw e
        } catch (e: Exception) {
            // 3. Fallo conserva archivo local y URI
            dao.actualizarEstado(evidenciaId, EvidenciaSyncState.FALLIDA.name)
        }
    }

    override suspend fun eliminar(evidenciaId: String) {
        withContext(Dispatchers.IO) {
            val entity = dao.obtenerPorId(evidenciaId)
            if (entity != null) {
                // Borrar registro en Room
                dao.eliminarPorId(evidenciaId)
                // Borrar archivo físico si es necesario (si está en almacenamiento interno gestionado por la app)
                try {
                    val uri = Uri.parse(entity.localUri)
                    contentResolver.delete(uri, null, null)
                } catch (e: Exception) {
                    // Ignorar si no se puede borrar el archivo físico (ej: URI de galería)
                }
            }
        }
    }
}

// Mapeadores
fun EvidenciaEntity.toDomain() = Evidencia(
    id = id,
    actividadId = actividadId,
    localUri = localUri,
    mimeType = mimeType,
    sizeBytes = sizeBytes,
    estado = EvidenciaSyncState.valueOf(estado),
    creadaEnEpochMillis = creadaEnEpochMillis
)

fun Evidencia.toEntity() = EvidenciaEntity(
    id = id,
    actividadId = actividadId,
    localUri = localUri,
    mimeType = mimeType,
    sizeBytes = sizeBytes,
    estado = estado.name,
    creadaEnEpochMillis = creadaEnEpochMillis
)

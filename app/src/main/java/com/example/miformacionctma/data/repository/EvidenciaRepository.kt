package com.example.miformacionctma.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.EstadoSincronizacion
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import com.example.miformacionctma.data.remote.EvidenciaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

class EvidenciaRepository(
    private val context: Context,
    private val evidenciaDao: EvidenciaDao,
    private val apiService: EvidenciaApiService
) {
    fun obtenerEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> {
        return evidenciaDao.obtenerEvidenciasPorActividad(actividadId)
    }

    suspend fun procesarYSubirEvidencia(actividadId: Long, uri: Uri): Result<EvidenciaEntity> = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            
            // Validar tipo MIME
            if (!mimeType.startsWith("image/") && mimeType != "application/pdf") {
                return@withContext Result.failure(IllegalArgumentException("Tipo de archivo no permitido: $mimeType"))
            }

            var sizeBytes = 0L
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeColIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeColIndex != -1 && cursor.moveToFirst()) {
                    sizeBytes = cursor.getLong(sizeColIndex)
                }
            }

            // Crear entidad en estado LOCAL
            val evidencia = EvidenciaEntity(
                actividadId = actividadId,
                localUri = uri.toString(),
                mimeType = mimeType,
                sizeBytes = sizeBytes,
                estado = EstadoSincronizacion.LOCAL,
                creadaEnEpochMillis = System.currentTimeMillis()
            )

            val id = evidenciaDao.insertarEvidencia(evidencia)
            val evidenciaGuardada = evidencia.copy(id = id)

            // Cambiar a SUBIENDO
            val evidenciaSubiendo = evidenciaGuardada.copy(estado = EstadoSincronizacion.SUBIENDO)
            evidenciaDao.actualizarEvidencia(evidenciaSubiendo)

            // Preparar el archivo para subir vía Retrofit multipart
            val file = copyUriToFile(uri)
            if (file == null) {
                val evidenciaFallida = evidenciaSubiendo.copy(estado = EstadoSincronizacion.FALLIDA)
                evidenciaDao.actualizarEvidencia(evidenciaFallida)
                return@withContext Result.failure(Exception("No se pudo leer el archivo local"))
            }

            val mediaType = MediaType.parse(mimeType)
            val requestFile = RequestBody.create(mediaType, file)
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val actividadIdBody = RequestBody.create(MediaType.parse("text/plain"), actividadId.toString())

            try {
                apiService.subirEvidencia(actividadIdBody, body)
                
                val evidenciaSincronizada = evidenciaSubiendo.copy(estado = EstadoSincronizacion.SINCRONIZADA)
                evidenciaDao.actualizarEvidencia(evidenciaSincronizada)
                Result.success(evidenciaSincronizada)
            } catch (e: Exception) {
                Log.e("EvidenciaRepository", "Error subiendo evidencia", e)
                val evidenciaFallida = evidenciaSubiendo.copy(estado = EstadoSincronizacion.FALLIDA)
                evidenciaDao.actualizarEvidencia(evidenciaFallida)
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e("EvidenciaRepository", "Error general procesando evidencia", e)
            Result.failure(e)
        }
    }

    private fun copyUriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempDir = File(context.cacheDir, "evidencias")
            if (!tempDir.exists()) tempDir.mkdirs()
            val tempFile = File(tempDir, "temp_${System.currentTimeMillis()}")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            Log.e("EvidenciaRepository", "Error copiando archivo", e)
            null
        }
    }
}

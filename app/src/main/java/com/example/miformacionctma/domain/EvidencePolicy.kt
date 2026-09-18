package com.example.miformacionctma.domain

import android.content.ContentResolver
import android.net.Uri
import java.io.InputStream

/**
 * Política de validación para evidencias (Semana 9).
 */
object EvidencePolicy {
    const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024 // 5 MB
    val ALLOWED_MIME_TYPES = listOf("image/jpeg", "image/png", "image/webp")

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validate(uri: Uri, contentResolver: ContentResolver): ValidationResult {
        val type = contentResolver.getType(uri)
        if (type !in ALLOWED_MIME_TYPES) {
            return ValidationResult(false, "Formato no permitido. Use JPEG, PNG o WEBP.")
        }

        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri)
            val size = inputStream?.available()?.toLong() ?: 0L
            
            if (size == 0L) {
                return ValidationResult(false, "El archivo está vacío.")
            }
            
            if (size > MAX_FILE_SIZE_BYTES) {
                return ValidationResult(false, "El archivo supera el límite de 5MB.")
            }
        } catch (e: Exception) {
            return ValidationResult(false, "No se pudo leer el archivo: ${e.message}")
        } finally {
            inputStream?.close()
        }

        return ValidationResult(true)
    }
}

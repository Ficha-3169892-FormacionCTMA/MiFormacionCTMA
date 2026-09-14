package com.example.miformacionctma.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileHelper {
    private const val MAX_SIZE = 5 * 1024 * 1024 // 5MB
    private val ALLOWED_TYPES = listOf("image/jpeg", "image/png", "image/webp")

    fun generarUriParaCamara(context: Context): Uri {
        val dir = File(context.filesDir, "evidencias").apply { mkdirs() }
        val file = File(dir, "TEMP_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun validarEvidencia(context: Context, uri: Uri): Boolean {
        return try {
            val resolver = context.contentResolver
            val mime = resolver.getType(uri) ?: ""
            val size = resolver.openAssetFileDescriptor(uri, "r")?.use { it.length } ?: 0
            
            mime in ALLOWED_TYPES && size <= MAX_SIZE
        } catch (e: Exception) {
            false
        }
    }
}

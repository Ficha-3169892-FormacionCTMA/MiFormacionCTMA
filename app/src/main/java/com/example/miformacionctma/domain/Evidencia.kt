package com.example.miformacionctma.domain

import kotlinx.serialization.Serializable

/**
 * Representa los estados de sincronización de una evidencia.
 */
enum class EvidenciaSyncState {
    LOCAL,      // Solo en el dispositivo
    SUBIENDO,   // Operación activa
    SINCRONIZADA, // Confirmada por el servidor
    FALLIDA     // Fallo en la operación
}

/**
 * Modelo de dominio para la evidencia fotográfica.
 */
@Serializable
data class Evidencia(
    val id: String,
    val actividadId: Long,
    val localUri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val estado: EvidenciaSyncState,
    val creadaEnEpochMillis: Long
)

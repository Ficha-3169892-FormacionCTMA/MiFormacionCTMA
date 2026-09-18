package com.example.miformacionctma.domain

enum class EvidenciaSyncState {
    LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA
}

data class Evidencia(
    val id: String,
    val actividadId: Long,
    val localUri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val estado: EvidenciaSyncState,
    val creadaEnEpochMillis: Long
)

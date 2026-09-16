package com.example.miformacionctma.data.local.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaSyncState

@Entity(
    tableName = "evidencias",
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("actividadId")]
)
data class EvidenciaEntity(
    @PrimaryKey val id: String,
    val actividadId: Long,
    val localUri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val estado: String,
    val creadaEnEpochMillis: Long
)

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

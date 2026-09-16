package com.example.miformacionctma.data.local.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

enum class EstadoSincronizacion {
    LOCAL, SUBIENDO, SINCRONIZADA, FALLIDA
}

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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actividadId: Long,
    val localUri: String,
    val mimeType: String,
    val sizeBytes: Long,
    val estado: EstadoSincronizacion,
    val creadaEnEpochMillis: Long
)

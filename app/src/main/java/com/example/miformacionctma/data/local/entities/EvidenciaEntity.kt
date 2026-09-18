package com.example.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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

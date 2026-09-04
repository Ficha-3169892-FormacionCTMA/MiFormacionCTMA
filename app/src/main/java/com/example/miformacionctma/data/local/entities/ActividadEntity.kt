package com.example.miformacionctma.data.local.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "actividades",
    foreignKeys = [
        ForeignKey(
            entity = CompetenciaEntity::class,
            parentColumns = ["id"],
            childColumns = ["competenciaId"],
            onDelete = ForeignKey.RESTRICT,
        )
    ],
    indices = [
        Index("competenciaId"), 
        Index(value = ["titulo"])
    ]
)
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String?,
    val progreso: Int,
    val prioridad: String,
    val competenciaId: Long,
    val fechaLimiteEpochMillis: Long,
    val completada: Boolean = false
)

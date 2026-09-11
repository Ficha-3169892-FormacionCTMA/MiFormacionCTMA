package com.example.miformacionctma.data.local.entities

import androidx.room3.Embedded
import androidx.room3.Relation

data class CompetenciaConActividades(
    @Embedded val competencia: CompetenciaEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["competenciaId"],
    )
    val actividades: List<ActividadEntity>
)

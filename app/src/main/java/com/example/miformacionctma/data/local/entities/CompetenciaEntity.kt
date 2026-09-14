package com.example.miformacionctma.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "competencias")
data class CompetenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
)

package com.example.miformacionctma.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEvidencia(evidencia: EvidenciaEntity): Long

    @Update
    suspend fun actualizarEvidencia(evidencia: EvidenciaEntity)

    @Query("SELECT * FROM evidencias WHERE actividadId = :actividadId ORDER BY creadaEnEpochMillis DESC")
    fun obtenerEvidenciasPorActividad(actividadId: Long): Flow<List<EvidenciaEntity>>
}

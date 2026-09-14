package com.example.miformacionctma.data.local.dao

import androidx.room3.*
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenciaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(evidencia: EvidenciaEntity): Long

    @Query("SELECT * FROM evidencias WHERE actividadId = :actividadId ORDER BY creadaEnEpochMillis DESC")
    fun observarEvidenciasPorActividad(actividadId: Long): Flow<List<EvidenciaEntity>>

    @Query("UPDATE evidencias SET estado = :nuevoEstado WHERE id = :evidenciaId")
    suspend fun actualizarEstado(evidenciaId: Long, nuevoEstado: String)

    @Delete
    suspend fun eliminar(evidencia: EvidenciaEntity)
}

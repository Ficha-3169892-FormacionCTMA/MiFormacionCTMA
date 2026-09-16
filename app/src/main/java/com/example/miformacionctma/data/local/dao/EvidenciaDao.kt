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
    @Query("SELECT * FROM evidencias WHERE actividadId = :actividadId")
    fun observarPorActividad(actividadId: Long): Flow<List<EvidenciaEntity>>

    @Query("SELECT * FROM evidencias WHERE id = :id")
    suspend fun obtenerPorId(id: String): EvidenciaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(evidencia: EvidenciaEntity)

    @Update
    suspend fun actualizar(evidencia: EvidenciaEntity)

    @Query("DELETE FROM evidencias WHERE id = :id")
    suspend fun eliminarPorId(id: String)

    @Query("UPDATE evidencias SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: String, nuevoEstado: String)
}

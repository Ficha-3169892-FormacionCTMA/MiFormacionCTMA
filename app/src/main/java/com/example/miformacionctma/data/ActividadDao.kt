package com.example.miformacionctma.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY fechaLimite ASC")
    fun getAllActividades(): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun getActividadById(id: Int): Actividad?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(actividad: Actividad)

    @Update
    suspend fun updateActividad(actividad: Actividad)

    @Delete
    suspend fun deleteActividad(actividad: Actividad)
}

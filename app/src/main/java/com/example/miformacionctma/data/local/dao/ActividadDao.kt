package com.example.miformacionctma.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.example.miformacionctma.data.local.entities.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY id ASC")
    fun observarTodos(): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    fun observarPorId(id: Long): Flow<ActividadEntity?>

    @Query(
        "SELECT * FROM actividades " +
        "WHERE LOWER(titulo) LIKE '%' || LOWER(:texto) || '%' " +
        "ORDER BY id ASC",
    )
    fun buscar(texto: String): Flow<List<ActividadEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(actividad: ActividadEntity)

    @Update
    suspend fun actualizar(actividad: ActividadEntity)

    @Query("DELETE FROM actividades WHERE id = :id")
    suspend fun eliminarPorId(id: Long): Int
}

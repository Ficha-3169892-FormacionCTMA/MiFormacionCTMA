package com.example.miformacionctma.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.miformacionctma.data.local.entities.ActividadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades ORDER BY id ASC")
    fun observarTodos(): Flow<List<ActividadEntity>>

    @Query("SELECT * FROM actividades WHERE id = :id")
    fun observarPorId(id: Long): Flow<ActividadEntity?>

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ActividadEntity?

    @Query(
        "SELECT * FROM actividades " +
        "WHERE LOWER(titulo) LIKE '%' || LOWER(:texto) || '%' " +
        "ORDER BY id ASC",
    )
    fun buscar(texto: String): Flow<List<ActividadEntity>>

    @Upsert
    suspend fun insertar(actividad: ActividadEntity): Long

    @Update
    suspend fun actualizar(actividad: ActividadEntity)

    // Corregido: Se usa 'completada' en lugar del campo inexistente 'estado'
    @Query("UPDATE actividades SET progreso = :progreso, completada = :completada WHERE id = :id")
    suspend fun actualizarProgreso(id: Long, progreso: Int, completada: Boolean)

    @Query("UPDATE actividades SET enlaceEvidencia = :enlace WHERE id = :id")
    suspend fun actualizarEnlaceEvidencia(id: Long, enlace: String?)

    @Query("DELETE FROM actividades WHERE id = :id")
    suspend fun eliminarPorId(id: Long): Int
}

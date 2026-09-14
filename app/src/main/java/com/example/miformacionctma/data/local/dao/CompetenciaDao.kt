package com.example.miformacionctma.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.miformacionctma.data.local.entities.CompetenciaConActividades
import com.example.miformacionctma.data.local.entities.CompetenciaEntity
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
interface CompetenciaDao {
    @Query("SELECT * FROM competencias ORDER BY nombre")
    fun observarTodos(): Flow<List<CompetenciaEntity>>

    @Transaction
    @Query("SELECT * FROM competencias ORDER BY nombre")
    fun observarConActividades(): Flow<List<CompetenciaConActividades>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(competencia: CompetenciaEntity)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarTodas(competencias: List<CompetenciaEntity>)
}

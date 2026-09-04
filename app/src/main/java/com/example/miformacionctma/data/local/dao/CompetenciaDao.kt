package com.example.miformacionctma.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(competencia: CompetenciaEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(competencias: List<CompetenciaEntity>)
}

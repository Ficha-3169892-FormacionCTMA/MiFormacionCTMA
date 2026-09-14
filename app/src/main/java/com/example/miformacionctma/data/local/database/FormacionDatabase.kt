package com.example.miformacionctma.data.local.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.dao.CompetenciaDao
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.data.local.entities.CompetenciaEntity
import com.example.miformacionctma.data.local.entities.EvidenciaEntity

@Database(
    entities = [ActividadEntity::class, CompetenciaEntity::class, EvidenciaEntity::class],
    version = 3,
    exportSchema = true,
)
abstract class FormacionDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao
    abstract fun competenciaDao(): CompetenciaDao
    abstract fun evidenciaDao(): EvidenciaDao

    companion object {
        @Volatile
        private var INSTANCE: FormacionDatabase? = null

        fun getDatabase(context: Context): FormacionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder<FormacionDatabase>(
                    context.applicationContext,
                    "mi_formacion_ctma.db"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

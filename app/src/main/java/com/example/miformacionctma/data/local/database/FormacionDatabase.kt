package com.example.miformacionctma.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.dao.CompetenciaDao
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.data.local.entities.CompetenciaEntity

@Database(
    entities = [ActividadEntity::class, CompetenciaEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class FormacionDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao
    abstract fun competenciaDao(): CompetenciaDao

    companion object {
        @Volatile
        private var INSTANCE: FormacionDatabase? = null

        fun getDatabase(context: Context): FormacionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FormacionDatabase::class.java,
                    "mi_formacion_ctma.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.example.miformacionctma

import android.app.Application
import com.example.miformacionctma.data.ActividadRepository
import com.example.miformacionctma.data.AppDatabase

class FormacionApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ActividadRepository(database.actividadDao()) }
}

package com.example.miformacionctma.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class ActividadDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: ActividadDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.actividadDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun HU01_InsertarYLeerActividad_DebePersistirCorrectamente() = runBlocking {
        val actividad = Actividad(
            titulo = "Tarea de Prueba",
            descripcion = "Probar persistencia",
            fechaLimite = Date(),
            progreso = 0.5f
        )
        dao.insertActividad(actividad)
        
        val lista = dao.getAllActividades().first()
        assertEquals(1, lista.size)
        assertEquals("Tarea de Prueba", lista[0].titulo)
    }
}

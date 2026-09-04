package com.example.miformacionctma.data.local.dao

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.miformacionctma.data.local.database.FormacionDatabase
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.data.local.entities.CompetenciaEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActividadDaoTest {
    private lateinit var db: FormacionDatabase
    private lateinit var dao: ActividadDao
    private lateinit var competenciaDao: CompetenciaDao

    @Before
    fun crearDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder<FormacionDatabase>(context)
            .setDriver(AndroidSQLiteDriver())
            .build()
        dao = db.actividadDao()
        competenciaDao = db.competenciaDao()
    }

    @After
    fun cerrarDb() = db.close()

    @Test
    fun insertar_y_observar_actividades() = runTest {
        // Necesitamos una competencia para la FK
        val competencia = CompetenciaEntity(id = 1L, nombre = "Desarrollo Móvil")
        competenciaDao.insertar(competencia)

        val actividad = ActividadEntity(
            id = 1L,
            titulo = "Aprender Room",
            descripcion = "Persistencia local",
            progreso = 50,
            prioridad = "ALTA",
            competenciaId = 1L,
            fechaLimiteEpochMillis = System.currentTimeMillis() + 86400000,
            completada = false,
        )

        dao.insertar(actividad)
        
        val lista = dao.observarTodos().first()
        assertEquals(1, lista.size)
        assertEquals("Aprender Room", lista[0].titulo)
    }

    @Test
    fun eliminar_actividad() = runTest {
        val competencia = CompetenciaEntity(id = 1L, nombre = "Desarrollo Móvil")
        competenciaDao.insertar(competencia)

        val actividad = ActividadEntity(
            id = 1L,
            titulo = "Aprender Room",
            descripcion = "Persistencia local",
            progreso = 50,
            prioridad = "ALTA",
            competenciaId = 1L,
            fechaLimiteEpochMillis = System.currentTimeMillis() + 86400000,
            completada = false,
        )

        dao.insertar(actividad)
        dao.eliminarPorId(1L)
        
        val lista = dao.observarTodos().first()
        assertEquals(0, lista.size)
    }
}

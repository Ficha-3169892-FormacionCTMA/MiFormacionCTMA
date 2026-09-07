package com.example.miformacionctma.data.local.migration

import androidx.room3.testing.MigrationTestHelper
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.miformacionctma.data.local.database.FormacionDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "migration-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        instrumentation = InstrumentationRegistry.getInstrumentation(),
        file = File(InstrumentationRegistry.getInstrumentation().targetContext.getDatabasePath(testDb).absolutePath),
        driver = AndroidSQLiteDriver(),
        databaseClass = FormacionDatabase::class,
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() = runTest {
        // Crear base de datos con la versión 1
        val connectionV1 = helper.createDatabase(1)

        // Insertar datos usando SQL
        connectionV1.execSQL(
            "INSERT INTO competencias (id, nombre) VALUES (1, 'Competencia 1')"
        )
        connectionV1.execSQL(
            "INSERT INTO actividades (id, titulo, descripcion, progreso, prioridad, competenciaId, fechaLimiteEpochMillis) " +
            "VALUES (1, 'Actividad 1', 'Desc', 0, 'MEDIA', 1, 1000000)"
        )
        connectionV1.close()

        // Ejecutar migración a versión 2
        helper.runMigrationsAndValidate(2)
    }
}

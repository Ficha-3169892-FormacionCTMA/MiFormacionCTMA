package com.example.miformacionctma

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.data.repository.SyncedActividadRepository
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Suite de Pruebas de Resiliencia (Semana 8).
 * Valida que el repositorio cumpla con el patrón Offline-First:
 * El sistema debe funcionar localmente aunque la nube falle.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ResilienciaRepositoryTest {

    private val testScope = TestScope()

    // Fake DAO para simular persistencia local
    private val fakeDao = object : ActividadDao {
        private val storage = mutableListOf<ActividadEntity>()
        private val flow = MutableStateFlow<List<ActividadEntity>>(emptyList())

        override fun observarTodos(): Flow<List<ActividadEntity>> = flow
        override fun observarPorId(id: Long): Flow<ActividadEntity?> = MutableStateFlow(storage.find { it.id == id })
        override fun buscar(texto: String): Flow<List<ActividadEntity>> = flow
        
        override suspend fun insertar(actividad: ActividadEntity): Long {
            val id = if (actividad.id == 0L) (storage.size + 1).toLong() else actividad.id
            storage.add(actividad.copy(id = id))
            flow.value = storage.toList()
            return id
        }

        override suspend fun actualizar(actividad: ActividadEntity) {
            storage.removeIf { it.id == actividad.id }
            storage.add(actividad)
            flow.value = storage.toList()
        }

        override suspend fun eliminarPorId(id: Long): Int {
            val removed = if (storage.removeIf { it.id == id }) 1 else 0
            flow.value = storage.toList()
            return removed
        }
    }

    @Test
    fun `cuando la nube falla el dato permanece en Room (Resiliencia)`() = runTest {
        // Arrange (Preparar)
        // El repositorio se inicializa con el fakeDao y un scope de prueba
        val repository = SyncedActividadRepository(fakeDao, testScope)
        val actividad = ActividadFormativa(
            id = 0,
            titulo = "Tarea Resiliente",
            progreso = 0,
            diasRestantes = 5,
            estado = EstadoActividad.PENDIENTE,
            prioridad = Prioridad.ALTA
        )

        // Act (Actuar)
        // Guardamos la actividad. Internamente intentará sincronizar con Supabase y fallará
        // (ya que no hay cliente real configurado en el entorno de pruebas unitarias).
        repository.guardar(actividad)

        // Assert (Afirmar)
        // Verificamos que, a pesar del posible fallo remoto, el dato está en la base local (SSOT)
        val listaLocal = repository.observarTodos().first()
        assertTrue("La lista local no debería estar vacía", listaLocal.isNotEmpty())
        assertEquals("Tarea Resiliente", listaLocal.first().titulo)
        
        println("Prueba de Resiliencia (Semana 8): Éxito. El dato se conservó localmente tras fallo de nube simulado.")
    }
}

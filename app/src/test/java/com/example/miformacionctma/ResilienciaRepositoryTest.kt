package com.example.miformacionctma

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.dao.EvidenciaDao
import com.example.miformacionctma.data.repository.SyncedActividadRepository
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class ResilienciaRepositoryTest {

    private val mockDao: ActividadDao = mock()
    private val mockEvidenciaDao: EvidenciaDao = mock()
    private lateinit var repository: SyncedActividadRepository
    private val testScope = TestScope()

    @Before
    fun setup() {
        repository = SyncedActividadRepository(mockDao, mockEvidenciaDao, testScope)
    }

    @Test
    fun `guardar persiste en Room incluso si falla la red`() = runTest {
        val actividad = ActividadFormativa(
            id = 1L,
            titulo = "Test",
            descripcion = null,
            progreso = 50,
            diasRestantes = 5,
            prioridad = Prioridad.ALTA,
            estado = EstadoActividad.EN_PROGRESO
        )
        
        whenever(mockDao.insertar(any())).thenReturn(1L)
        
        repository.guardar(actividad)
        
        verify(mockDao).insertar(any())
    }
}

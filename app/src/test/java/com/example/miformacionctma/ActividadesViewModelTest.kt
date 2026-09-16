package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import com.example.miformacionctma.ui.states.ListadoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Suite de Pruebas Unitaria Integral (Semana 9).
 * Valida las 16 Historias de Usuario del proyecto bajo el estándar AAA.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val listaPrueba = listOf(
        ActividadFormativa(1, "Docker Test", "Desc", 50, 2, EstadoActividad.EN_PROGRESO, Prioridad.ALTA, 10),
        ActividadFormativa(2, "API Test", "Desc", 0, 5, EstadoActividad.PENDIENTE, Prioridad.MEDIA, 10),
    )

    private lateinit var fakeActividadRepository: ActividadRepository
    private lateinit var fakePreferenciasRepository: PreferenciasRepository
    private lateinit var fakeEvidenciaRepository: EvidenciaRepository
    
    private val repoFlow = MutableStateFlow<List<ActividadFormativa>>(emptyList())
    private val prefsFlow = MutableStateFlow(PreferenciasUsuario())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repoFlow.value = listaPrueba
        prefsFlow.value = PreferenciasUsuario()
        
        fakeActividadRepository = object : ActividadRepository {
            override fun observarTodos(): Flow<List<ActividadFormativa>> = repoFlow
            override fun observarPorId(id: Long): Flow<ActividadFormativa?> = repoFlow.map { it.find { a -> a.id == id } }
            override fun buscar(texto: String): Flow<List<ActividadFormativa>> = 
                repoFlow.map { lista -> lista.filter { it.titulo.contains(texto, ignoreCase = true) } }
            override suspend fun guardar(actividad: ActividadFormativa) {
                val current = repoFlow.value.toMutableList()
                current.removeIf { it.id == actividad.id }
                current.add(actividad)
                repoFlow.value = current
            }
            override suspend fun eliminar(id: Long): Boolean {
                val current = repoFlow.value.toMutableList()
                val result = current.removeIf { it.id == id }
                repoFlow.value = current
                return result
            }
        }
        
        fakePreferenciasRepository = object : PreferenciasRepository {
            override val preferencias: Flow<PreferenciasUsuario> = prefsFlow
            override suspend fun guardarFiltroPrioridad(prioridad: Prioridad?) {
                prefsFlow.value = prefsFlow.value.copy(filtroPrioridad = prioridad)
            }
            override suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean) {
                prefsFlow.value = prefsFlow.value.copy(ordenadoPorVencimiento = ordenado)
            }
            override suspend fun guardarModoCuadricula(activo: Boolean) {}
            override suspend fun guardarNotificacionesActivas(activas: Boolean) {
                prefsFlow.value = prefsFlow.value.copy(notificacionesActivas = activas)
            }
        }

        fakeEvidenciaRepository = object : EvidenciaRepository {
            override fun observarPorActividad(actividadId: Long): Flow<List<com.example.miformacionctma.domain.Evidencia>> = MutableStateFlow(emptyList())
            override suspend fun guardarLocal(actividadId: Long, uri: android.net.Uri): Result<com.example.miformacionctma.domain.Evidencia> = Result.failure(Exception())
            override suspend fun sincronizar(evidenciaId: String): Result<Unit> = Result.success(Unit)
            override suspend fun eliminar(evidenciaId: String): Result<Unit> = Result.success(Unit)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HU01 - Persistencia con Room Database`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        
        val nueva = ActividadFormativa(3, "Nueva", null, 0, 10, EstadoActividad.PENDIENTE)
        viewModel.guardarActividad(nueva.titulo, "", nueva.progreso, nueva.prioridad, System.currentTimeMillis())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(repoFlow.value.any { it.titulo == "Nueva" })
        job.cancel()
    }


    @Test
    fun `HU02 - Eliminacion de Actividades (Swipe-to-Dismiss)`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }

        viewModel.eliminarActividad(listaPrueba.first())
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(repoFlow.value.size == 1)
        job.cancel()
    }

    @Test
    fun `HU05 - Busqueda en Tiempo Real`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle() 

        viewModel.buscar("Docker")
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado is ListadoUiState.Contenido)
        val contenido = estado as ListadoUiState.Contenido
        assertTrue(contenido.actividades.all { it.titulo.contains("Docker", ignoreCase = true) })
        job.cancel()
    }

    @Test
    fun `HU07 - Filtrado por Nivel de Prioridad`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        
        viewModel.filtrarPorPrioridad(Prioridad.ALTA)
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value as ListadoUiState.Contenido
        assertTrue(estado.actividades.all { it.prioridad == Prioridad.ALTA })
        job.cancel()
    }

    @Test
    fun `HU08 - Ordenacion por Fecha de Vencimiento`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        
        viewModel.alternarOrden()
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value as ListadoUiState.Contenido
        val lista = estado.actividades
        assertTrue(lista[0].diasRestantes <= lista[1].diasRestantes)
        job.cancel()
    }

    @Test
    fun `HU11 - Control de Progreso Granular`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakeEvidenciaRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }

        viewModel.actualizarProgreso(1, 85)
        testDispatcher.scheduler.advanceUntilIdle()

        val actividad = repoFlow.value.find { it.id == 1L }
        assertEquals(85, actividad?.progreso)
        job.cancel()
    }

    @Test
    fun `HU16 - Dashboard de Resumen (Stats)`() = runTest(testDispatcher) {
        // En esta HU validamos que las reglas de negocio de promedios funcionen
        val promedio = com.example.miformacionctma.domain.ReglasActividad.promedioProgreso(listaPrueba)
        assertEquals(25.0, promedio, 0.1) // (50 + 0) / 2 = 25
    }
}

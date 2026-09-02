package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.ActividadesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private val listaPrueba = listOf(
        ActividadFormativa(1, "Docker Test", "Desc", 50, 2, EstadoActividad.EN_PROGRESO, Prioridad.ALTA),
        ActividadFormativa(2, "API Test", "Desc", 0, 5, EstadoActividad.PENDIENTE, Prioridad.MEDIA),
    )

    private lateinit var fakeActividadRepository: ActividadRepository
    private lateinit var fakePreferenciasRepository: PreferenciasRepository
    
    private val repoFlow = MutableStateFlow<List<ActividadFormativa>>(emptyList())
    private val prefsFlow = MutableStateFlow(PreferenciasUsuario())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repoFlow.value = listaPrueba
        prefsFlow.value = PreferenciasUsuario()
        
        fakeActividadRepository = object : ActividadRepository {
            override fun observarTodos(): Flow<List<ActividadFormativa>> = repoFlow
            override fun observarPorId(id: Long): Flow<ActividadFormativa?> = MutableStateFlow(null)
            override fun buscar(texto: String): Flow<List<ActividadFormativa>> = MutableStateFlow(emptyList())
            override suspend fun guardar(actividad: ActividadFormativa) {}
            override suspend fun eliminar(id: Long): Boolean = true
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
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HU05 - Busqueda en Tiempo Real`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakePreferenciasRepository)
        
        // Iniciamos la recolección para activar el StateFlow (HU 05, 06, 07, 08)
        val job = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle() 

        viewModel.buscar("Docker")
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(
            "La lista debe contener solo la actividad con 'Docker'",
            estado.actividadesVisibles.all { it.titulo.contains("Docker", ignoreCase = true) },
        )
        job.cancel()
    }

    @Test
    fun `HU06 - Visualizacion de Prioridad con Chips`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value
        val lista = estado.actividadesVisibles
        
        assertTrue("La lista inicial en el estado debe tener 2 elementos", lista.size == 2)
        assertTrue(
            "Cada actividad expuesta debe tener su prioridad",
            lista.any { it.prioridad == Prioridad.ALTA }
        )
        job.cancel()
    }

    @Test
    fun `HU07 - Filtrado por Nivel de Prioridad`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.filtrarPorPrioridad(Prioridad.ALTA)
        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(
            "Todas las actividades visibles deben tener prioridad ALTA",
            estado.actividadesVisibles.all { it.prioridad == Prioridad.ALTA },
        )
        job.cancel()
    }

    @Test
    fun `HU08 - Ordenacion por Fecha de Vencimiento`() = runTest(testDispatcher) {
        val viewModel = ActividadesViewModel(fakeActividadRepository, fakePreferenciasRepository)
        val job = launch { viewModel.uiState.collect {} }
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.alternarOrden()
        testDispatcher.scheduler.advanceUntilIdle()

        val lista = viewModel.uiState.value.actividadesVisibles
        if (lista.size >= 2) {
            for (i in 0 until (lista.size - 1)) {
                assertTrue(
                    "La actividad en index $i debe vencer antes que la de index ${i+1}",
                    lista[i].diasRestantes <= lista[i + 1].diasRestantes,
                )
            }
        }
        job.cancel()
    }
}

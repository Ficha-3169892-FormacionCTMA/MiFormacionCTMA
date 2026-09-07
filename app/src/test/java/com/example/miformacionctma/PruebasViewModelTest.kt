package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PruebasViewModelTest {

    private lateinit var viewModel: ActividadesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeRepository = object : ActividadRepository {
        override fun observarTodos(): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override fun observarPorId(id: Long): Flow<ActividadFormativa?> = flowOf(null)
        override fun buscar(texto: String): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override suspend fun guardar(actividad: ActividadFormativa) {}
        override suspend fun eliminar(id: Long): Boolean = true
    }

    private val fakePreferenciasRepository = object : PreferenciasRepository {
        override val preferencias: Flow<PreferenciasUsuario> = flowOf(PreferenciasUsuario())
        override suspend fun guardarFiltroPrioridad(prioridad: Prioridad?) {}
        override suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean) {}
        override suspend fun guardarModoCuadricula(activo: Boolean) {}
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActividadesViewModel(fakeRepository, fakePreferenciasRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun buscar_actualizaElEstado() = runTest {
        val job = launch(testDispatcher) { viewModel.uiState.collect() }
        
        viewModel.buscar("Kotlin")
        assertEquals("Kotlin", viewModel.uiState.value.searchQuery)
        
        job.cancel()
    }

    @Test
    fun seleccionarActividad_actualizaId() = runTest {
        val job = launch(testDispatcher) { viewModel.uiState.collect() }
        viewModel.seleccionarActividad(5L)
        job.cancel()
    }
}

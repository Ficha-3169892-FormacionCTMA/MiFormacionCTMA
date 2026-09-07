package com.example.miformacionctma.ui.viewmodel

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.repository.ActividadRepository
import com.example.miformacionctma.ui.ListadoUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeRepository = object : ActividadRepository {
        override fun observarActividades(): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override fun buscar(query: String): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override suspend fun guardar(actividad: ActividadFormativa) {}
        override suspend fun eliminar(id: String): Boolean = true
    }

    private lateinit var viewModel: ActividadesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActividadesViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState inicial debe ser Vacio cuando el repositorio no tiene datos`() = runTest {
        // Necesitamos recolectar el stateIn(WhileSubscribed) para que se active el flujo
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        
        val currentState = viewModel.uiState.value
        assertTrue("El estado actual es $currentState", currentState is ListadoUiState.Vacio)
        job.cancel()
    }

    @Test
    fun `al cambiar busqueda el uiState sigue siendo reactivo`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        
        viewModel.cambiarBusqueda("Kotlin")
        
        val currentState = viewModel.uiState.value
        assertTrue("El estado actual tras búsqueda es $currentState", currentState is ListadoUiState.Vacio)
        job.cancel()
    }
}
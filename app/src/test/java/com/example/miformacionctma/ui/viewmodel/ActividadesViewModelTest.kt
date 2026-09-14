package com.example.miformacionctma.ui.viewmodel

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.data.local.entities.EvidenciaEntity
import com.example.miformacionctma.ui.states.ListadoUiState
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
        override fun observarTodos(): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override fun observarPorId(id: Long): Flow<ActividadFormativa?> = flowOf(null)
        override fun buscar(texto: String): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override suspend fun guardar(actividad: ActividadFormativa): Long = 1L
        override suspend fun eliminar(id: Long): Boolean = true
        override fun observarEvidencias(actividadId: Long): Flow<List<EvidenciaEntity>> = flowOf(emptyList())
        override suspend fun guardarEvidencia(evidencia: EvidenciaEntity) {}
    }

    private val fakePreferenciasRepository = object : PreferenciasRepository {
        override val preferencias: Flow<PreferenciasUsuario> = flowOf(PreferenciasUsuario())
        override suspend fun guardarFiltroPrioridad(prioridad: Prioridad?) {}
        override suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean) {}
        override suspend fun guardarModoCuadricula(activo: Boolean) {}
    }

    private lateinit var viewModel: ActividadesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActividadesViewModel(fakeRepository, fakePreferenciasRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState inicial debe ser reactivo cuando el repositorio no tiene datos`() = runTest {
        val job = backgroundScope.launch(testDispatcher) { viewModel.uiState.collect() }
        
        val currentState = viewModel.uiState.value
        assertTrue("El estado actual es $currentState", currentState is ListadoUiState.Vacio)
        job.cancel()
    }
}

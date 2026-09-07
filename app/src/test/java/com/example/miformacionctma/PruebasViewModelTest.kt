package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.repository.ActividadRepository
import com.example.miformacionctma.ui.OperacionUiState
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PruebasViewModelTest {

    private lateinit var viewModel: ActividadesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeRepository = object : ActividadRepository {
        override fun observarActividades(): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override fun buscar(query: String): Flow<List<ActividadFormativa>> = flowOf(emptyList())
        override suspend fun guardar(actividad: ActividadFormativa) {}
        override suspend fun eliminar(id: String): Boolean = true
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ActividadesViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun actualizarProgreso_valorValidoIntermedio_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 50)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Exitosa)
    }

    @Test
    fun actualizarProgreso_limiteInferiorExacto0_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 0)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Exitosa)
    }

    @Test
    fun actualizarProgreso_limiteSuperiorExacto100_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 100)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Exitosa)
    }

    @Test
    fun actualizarProgreso_limiteInmediatoInferiorFueraDeRango_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, -1)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Fallida)
        assertEquals("Porcentaje inválido (-1%). Debe estar entre 0 y 100.", (estado as OperacionUiState.Fallida).mensaje)
    }

    @Test
    fun actualizarProgreso_limiteInmediatoSuperiorFueraDeRango_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 101)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Fallida)
        assertEquals("Porcentaje inválido (101%). Debe estar entre 0 y 100.", (estado as OperacionUiState.Fallida).mensaje)
    }

    @Test
    fun actualizarProgreso_actividadVencidaDiasCero_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 0)

        viewModel.actualizarProgreso(actividad, 80)

        val estado = viewModel.operacion.value
        assertTrue(estado is OperacionUiState.Fallida)
        assertEquals("No se puede editar: la actividad está vencida.", (estado as OperacionUiState.Fallida).mensaje)
    }

    @Test
    fun reiniciarEstado_restableceEstadoAInactivo() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, -10)
        assertTrue(viewModel.operacion.value is OperacionUiState.Fallida)

        viewModel.reiniciarEstadoOperacion()

        assertTrue(viewModel.operacion.value is OperacionUiState.Inactiva)
    }

    private fun crearActividadPrueba(diasRestantes: Int): ActividadFormativa {
        return ActividadFormativa(
            id = 1,
            titulo = "Actividad de Prueba",
            descripcion = "Descripción de prueba unitaria",
            progreso = 10,
            prioridad = Prioridad.ALTA,
            diasRestantes = diasRestantes
        )
    }
}
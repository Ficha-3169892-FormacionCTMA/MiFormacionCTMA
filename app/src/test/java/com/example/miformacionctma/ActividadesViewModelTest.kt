package com.example.miformacionctma

import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.ActividadRepository
import com.example.miformacionctma.ui.ActividadesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ActividadRepository
    private lateinit var viewModel: ActividadesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        
        // Simulamos que el repo devuelve una lista vacía al inicio
        whenever(repository.allActividades).thenReturn(flowOf(emptyList()))
        viewModel = ActividadesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `HU04 - Historia de Actividades Finalizadas`() = runTest {
        // Necesitamos recolectar el StateFlow para que se active debido a WhileSubscribed
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        // Ejecución
        viewModel.toggleMostrarFinalizadas()
        advanceUntilIdle()
        
        // Verificación
        assertEquals(true, viewModel.uiState.value.mostrarFinalizadas)
        
        collectJob.cancel()
    }

    @Test
    fun `HU03 - Edicion de Actividades Existentes`() = runTest {
        val actividadId = 1
        val actividadOriginal = Actividad(actividadId, "Original", "Desc", Date(), 0f)
        
        // Configurar mock
        whenever(repository.getActividadById(actividadId)).thenReturn(actividadOriginal)

        // Ejecución
        viewModel.editarActividad(actividadId, "Editado", "Nueva Desc")
        advanceUntilIdle()

        // Verificación
        verify(repository).updateActividad(argThat { 
            this.id == actividadId && this.titulo == "Editado" && this.descripcion == "Nueva Desc"
        })
    }

    @Test
    fun `HU01 Persistencia con Room Database`() = runTest {
        // Ejecución
        viewModel.agregarActividad("Nueva Tarea", "Desc")
        advanceUntilIdle()

        // Verificación
        verify(repository).insertActividad(any())
    }

    @Test
    fun `HU02 - Eliminación de actividades (deslizar para descartar)`() = runTest {
        val actividad = Actividad(1, "Test", "Desc", Date(), 0f)
        
        // Ejecución
        viewModel.eliminarActividad(actividad)
        advanceUntilIdle()

        // Verificación
        verify(repository).deleteActividad(actividad)
    }
}

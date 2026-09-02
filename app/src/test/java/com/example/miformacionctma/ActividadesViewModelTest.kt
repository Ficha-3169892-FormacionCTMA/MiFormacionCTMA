package com.example.miformacionctma

import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.ActividadRepository
import com.example.miformacionctma.ui.ActividadesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
    fun `HU04 - Al cambiar el filtro, el estado debe actualizar mostrarFinalizadas`() = runTest {
        // Ejecución
        viewModel.toggleMostrarFinalizadas()
        
        // Verificación
        assertEquals(true, viewModel.uiState.value.mostrarFinalizadas)
    }

    @Test
    fun `HU03 - Al editar una actividad, se debe llamar al repositorio con los datos nuevos`() = runTest {
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
    fun `HU01 - Al agregar actividad se debe persistir en el repositorio`() = runTest {
        // Ejecución
        viewModel.agregarActividad("Nueva Tarea", "Desc")
        advanceUntilIdle()

        // Verificación
        verify(repository).insertActividad(any())
    }

    @Test
    fun `HU02 - Al eliminar actividad se debe llamar al borrado en el repositorio`() = runTest {
        val actividad = Actividad(1, "Test", "Desc", Date(), 0f)
        
        // Ejecución
        viewModel.eliminarActividad(actividad)
        advanceUntilIdle()

        // Verificación
        verify(repository).deleteActividad(actividad)
    }
}

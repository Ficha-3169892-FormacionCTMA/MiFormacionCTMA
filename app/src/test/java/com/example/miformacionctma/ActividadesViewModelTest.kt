package com.example.miformacionctma

import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.ActividadesViewModel
import org.junit.Assert.assertTrue
import org.junit.Test

class ActividadesViewModelTest {

    @Test
    fun `HU05 - Busqueda por titulo filtra la lista correctamente`() {
        // Arrange
        val viewModel = ActividadesViewModel()
        
        // Act
        viewModel.buscar("Docker")

        // Assert
        val estado = viewModel.uiState.value
        assertTrue(
            "La lista debe contener la palabra buscada",
            estado.actividadesVisibles.all { it.titulo.contains("Docker", ignoreCase = true) },
        )
    }

    @Test
    fun `HU07 - Filtrado por prioridad ALTA funciona satisfactoriamente`() {
        // Arrange
        val viewModel = ActividadesViewModel()

        // Act
        viewModel.filtrarPorPrioridad(Prioridad.ALTA)

        // Assert
        val estado = viewModel.uiState.value
        assertTrue(
            "Todas las actividades deben tener prioridad ALTA",
            estado.actividadesVisibles.all { it.prioridad == Prioridad.ALTA },
        )
    }

    @Test
    fun `HU08 - Ordenacion por vencimiento coloca los plazos mas cortos primero`() {
        // Arrange
        val viewModel = ActividadesViewModel()

        // Act
        viewModel.alternarOrden()

        // Assert
        val lista = viewModel.uiState.value.actividadesVisibles
        if (lista.size >= 2) {
            for (i in 0 until (lista.size - 1)) {
                assertTrue(
                    "La actividad en i debe vencer antes o igual que en i+1",
                    lista[i].diasRestantes <= lista[i + 1].diasRestantes,
                )
            }
        }
    }

    @Test
    fun `Filtros combinados - Busqueda y Prioridad funcionan simultaneamente`() {
        // Arrange
        val viewModel = ActividadesViewModel()

        // Act: Buscamos "API" y filtramos por "ALTA"
        viewModel.buscar("API")
        viewModel.filtrarPorPrioridad(Prioridad.ALTA)

        // Assert
        val estado = viewModel.uiState.value
        assertTrue(
            "Debe cumplir con el titulo y la prioridad",
            estado.actividadesVisibles.all { 
                (it.titulo.contains("API", ignoreCase = true)) && (it.prioridad == Prioridad.ALTA) 
            },
        )
    }
}

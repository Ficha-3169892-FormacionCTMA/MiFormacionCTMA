package com.example.miformacionctma

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.states.ListadoUiState
import org.junit.Rule
import org.junit.Test

class ActividadesUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun HU02_GestoSwipeALaIzquierda_DebeLlamarAlCallbackEliminar() {
        var actividadEliminada = false
        val actividadPrueba = ActividadFormativa(
            id = 1L,
            titulo = "Tarea para Borrar",
            descripcion = "Prueba de swipe",
            progreso = 0,
            diasRestantes = 5,
            estado = EstadoActividad.PENDIENTE,
            prioridad = Prioridad.MEDIA,
            horas = 10
        )

        composeTestRule.setContent {
            PantallaActividades(
                listadoUiState = ListadoUiState.Contenido(listOf(actividadPrueba)),
                searchQuery = "",
                onSearchChange = {},
                prioridadSeleccionada = null,
                onPrioridadFilterClick = {},
                ordenadoPorVencimiento = false,
                onSortClick = {},
                onActividadClick = {},
                onCrearClick = {},
                onEliminarActividad = { actividadEliminada = true }
            )
        }

        // Simular gesto de deslizamiento a la izquierda
        composeTestRule.onNodeWithText("Tarea para Borrar")
            .performTouchInput {
                swipeLeft()
            }

        // Verificar si el estado cambió a través del callback
        assert(actividadEliminada)
    }
}

package com.example.miformacionctma

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.ui.screens.PantallaActividades
import org.junit.Rule
import org.junit.Test
import java.util.Date

class ActividadesUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun HU02_GestoSwipeALaIzquierda_DebeLlamarAlCallbackEliminar() {
        var actividadEliminada = false
        val actividadPrueba = Actividad(
            id = 1,
            titulo = "Tarea para Borrar",
            descripcion = "Prueba de swipe",
            fechaLimite = Date(),
            progreso = 0f
        )

        composeTestRule.setContent {
            PantallaActividades(
                actividades = listOf(actividadPrueba),
                mostrarFinalizadas = false,
                onActividadClick = {},
                onCrearActividadClick = {},
                onToggleFinalizadas = {},
                onDeleteActividad = { actividadEliminada = true }
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

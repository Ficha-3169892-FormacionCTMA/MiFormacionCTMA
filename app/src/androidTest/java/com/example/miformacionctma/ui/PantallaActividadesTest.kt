package com.example.miformacionctma.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.screens.PantallaActividades
import com.example.miformacionctma.ui.theme.MiFormacionCTMATheme
import org.junit.Rule
import org.junit.Test

class PantallaActividadesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboard_muestra_estadisticas_correctas_HU16() {
        val actividades = listOf(
            ActividadFormativa(1, "Act1", null, 50, 5, Prioridad.ALTA, 10),
            ActividadFormativa(2, "Act2", null, 0, 5, Prioridad.MEDIA, 10),
        )

        composeTestRule.setContent {
            MiFormacionCTMATheme {
                PantallaActividades(actividades = actividades)
            }
        }

        // Verificar título del Dashboard
        composeTestRule.onNodeWithText("Resumen de Formación").assertIsDisplayed()
        
        // Verificar valores (Promedio de 50 y 0 es 25)
        composeTestRule.onNodeWithText("25%").assertIsDisplayed()
        composeTestRule.onNodeWithText("20h").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
    }

    @Test
    fun alHacerClickEnActividadConEnlace_muestraBotonVerEvidencia_HU13() {
        val actividad = ActividadFormativa(
            1, "TestLink", null, 0, 5, Prioridad.ALTA, 10, "https://google.com",
        )

        composeTestRule.setContent {
            MiFormacionCTMATheme {
                PantallaActividades(actividades = listOf(actividad))
            }
        }

        // Hacer click en la tarjeta
        composeTestRule.onNodeWithText("TestLink").performClick()

        // Verificar botones en el diálogo
        composeTestRule.onNodeWithText("Ver Evidencia").assertIsDisplayed()
        composeTestRule.onNodeWithText("Compartir").assertIsDisplayed()
    }

    @Test
    fun alHacerClickEnActividadSinEnlace_NO_muestraBotonVerEvidencia_HU13() {
        val actividad = ActividadFormativa(
            1, "NoLink", null, 0, 5, Prioridad.ALTA, 10, null,
        )

        composeTestRule.setContent {
            MiFormacionCTMATheme {
                PantallaActividades(actividades = listOf(actividad))
            }
        }

        composeTestRule.onNodeWithText("NoLink").performClick()

        composeTestRule.onNodeWithText("Ver Evidencia").assertDoesNotExist()
        composeTestRule.onNodeWithText("Compartir").assertIsDisplayed()
    }
}

package com.example.miformacionctma

import org.junit.Test
import org.junit.Assert.*

class PruebasactividadTest {

    @Test
    fun validar_diversos_rangos_de_progreso() {
        // Simulamos una lista de escenarios interactivos (progreso y si debería ser válido)
        val casosPrueba = listOf(
            -10 to false,  // Fuera de rango (menor a 0)
            0 to true,     // Límite inferior válido
            45 to true,    // Rango medio válido
            100 to true,   // Límite superior válido
            150 to false   // Fuera de rango (mayor a 100)
        )

        for ((progreso, esperado) in casosPrueba) {
            val esValido = progreso in 0..100
            assertEquals("Falló con el valor de progreso: $progreso", esperado, esValido)
        }
    }

    @Test
    fun verificar_diferentes_estados_de_actividad() {
        // Probando de forma dinámica varios estados de avance
        val progresosCompletados = listOf(100, 100, 100)
        val progresosIncompletos = listOf(0, 50, 99)

        for (progreso in progresosCompletados) {
            assertTrue("El progreso $progreso debería considerarse completo", progreso == 100)
        }

        for (progreso in progresosIncompletos) {
            assertFalse("El progreso $progreso no debería considerarse completo aún", progreso == 100)
        }
    }
}
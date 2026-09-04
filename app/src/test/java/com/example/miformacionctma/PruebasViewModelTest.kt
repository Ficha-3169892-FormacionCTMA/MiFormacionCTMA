package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.ui.viewmodel.ActividadesViewModel
import com.example.miformacionctma.ui.viewmodel.EstadoProgresoUI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PruebasViewModelTest {

    private lateinit var viewModel: ActividadesViewModel

    @Before
    fun setUp() {
        viewModel = ActividadesViewModel()
    }

    // =========================================================================
    // CASOS VÁLIDOS (Valores permitidos dentro del rango 0 - 100)
    // =========================================================================

    @Test
    fun actualizarProgreso_valorValidoIntermedio_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 50)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Exito)
        assertEquals("Progreso actualizado correctamente a 50%", (estado as EstadoProgresoUI.Exito).mensaje)
    }

    @Test
    fun actualizarProgreso_limiteInferiorExacto0_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 0)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Exito)
    }

    @Test
    fun actualizarProgreso_limiteSuperiorExacto100_retornaExito() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 100)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Exito)
    }

    // =========================================================================
    // CASOS INVÁLIDOS (Valores fuera de rango - Porcentajes erróneos)
    // =========================================================================

    @Test
    fun actualizarProgreso_limiteInmediatoInferiorFueraDeRango_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, -1)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
        assertEquals("Porcentaje inválido (-1%). Debe estar entre 0 y 100.", (estado as EstadoProgresoUI.Error).mensaje)
    }

    @Test
    fun actualizarProgreso_limiteInmediatoSuperiorFueraDeRango_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 101)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
        assertEquals("Porcentaje inválido (101%). Debe estar entre 0 y 100.", (estado as EstadoProgresoUI.Error).mensaje)
    }

    @Test
    fun actualizarProgreso_porcentajeMuyNegativo_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, -50)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
    }

    @Test
    fun actualizarProgreso_porcentajeMuyAlto_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, 200)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
    }

    // =========================================================================
    // CASOS INVÁLIDOS (Reglas de Negocio: Fechas y Estado Vencido)
    // =========================================================================

    @Test
    fun actualizarProgreso_actividadVencidaDiasCero_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = 0)

        viewModel.actualizarProgreso(actividad, 80)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
        assertEquals("No se puede editar: la actividad está vencida.", (estado as EstadoProgresoUI.Error).mensaje)
    }

    @Test
    fun actualizarProgreso_actividadVencidaDiasNegativos_retornaError() {
        val actividad = crearActividadPrueba(diasRestantes = -3)

        viewModel.actualizarProgreso(actividad, 80)

        val estado = viewModel.estadoUI.value
        assertTrue(estado is EstadoProgresoUI.Error)
        assertEquals("No se puede editar: la actividad está vencida.", (estado as EstadoProgresoUI.Error).mensaje)
    }

    // =========================================================================
    // PRUEBAS DE ESTADO DE LA INTERFAZ
    // =========================================================================

    @Test
    fun reiniciarEstado_restableceEstadoAReposo() {
        val actividad = crearActividadPrueba(diasRestantes = 5)

        viewModel.actualizarProgreso(actividad, -10)
        assertTrue(viewModel.estadoUI.value is EstadoProgresoUI.Error)

        viewModel.reiniciarEstado()

        assertTrue(viewModel.estadoUI.value is EstadoProgresoUI.Reposo)
    }

    // Función auxiliar para instanciar objetos rápidamente
    private fun crearActividadPrueba(diasRestantes: Int): ActividadFormativa {
        return ActividadFormativa(
            id = 1,
            titulo = "Actividad de Prueba",
            descripcion = "Descripción de prueba unitaria",
            progreso = 10,
            prioridad = Prioridad.ALTA,
            diasRestantes = diasRestantes,
        )
    }
}

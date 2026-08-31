package com.example.miformacionctma

import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.ReglasActividad
import org.junit.Test
import org.junit.Assert.*

class ReglasActividadTest {

    @Test
    fun test_1_titulo_vacio_debe_fallar() {
        val error = ReglasActividad.validarTitulo("", true)
        assertEquals("Escribe un título", error)
    }

    @Test
    fun test_2_titulo_corto_debe_fallar() {
        val error = ReglasActividad.validarTitulo("Hi", true)
        assertEquals("Usa al menos 3 caracteres", error)
    }

    @Test
    fun test_3_titulo_largo_debe_fallar() {
        val tituloLargo = "A".repeat(81)
        val error = ReglasActividad.validarTitulo(tituloLargo, true)
        assertEquals("Usa máximo 80 caracteres", error)
    }

    @Test
    fun test_4_descripcion_larga_debe_detectarse() {
        val descLarga = "A".repeat(241)
        val error = ReglasActividad.validarDescripcion(descLarga)
        assertEquals("Máximo 240 caracteres", error)
    }

    @Test
    fun test_5_progreso_100_es_COMPLETADA() {
        val estado = ReglasActividad.obtenerEstado(100, 5)
        assertEquals(EstadoActividad.COMPLETADA, estado)
    }

    @Test
    fun test_6_dias_negativos_es_VENCIDA() {
        val estado = ReglasActividad.obtenerEstado(50, -1)
        assertEquals(EstadoActividad.VENCIDA, estado)
    }

    @Test
    fun test_7_progreso_0_es_PENDIENTE() {
        val estado = ReglasActividad.obtenerEstado(0, 10)
        assertEquals(EstadoActividad.PENDIENTE, estado)
    }

    @Test
    fun test_8_fecha_pasada_debe_fallar() {
        val hoy = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        
        val ayer = hoy.timeInMillis - (24 * 60 * 60 * 1000)
        val error = ReglasActividad.validarFecha(ayer)
        assertEquals("La fecha no puede ser anterior a hoy", error)
    }
}

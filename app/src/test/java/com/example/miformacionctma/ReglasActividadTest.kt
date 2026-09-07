package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.EstadoActividad
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import org.junit.Assert.*
import org.junit.Test

class ReglasActividadTest {

    @Test
    fun `validarActividad detecta errores en titulo y progreso`() {
        val errores = ReglasActividad.validarActividad("", -5)
        assertTrue(errores.contains("El título no puede estar vacío."))
        assertTrue(errores.contains("El progreso debe estar entre 0 y 100."))
    }

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

    @Test
    fun `promedioProgreso calcula correctamente el promedio (HU16)`() {
        val actividades = listOf(
            ActividadFormativa(1, "A", null, 40, 5, EstadoActividad.EN_PROGRESO, Prioridad.ALTA, 10),
            ActividadFormativa(2, "B", null, 60, 5, EstadoActividad.EN_PROGRESO, Prioridad.MEDIA, 20),
        )
        val promedio = ReglasActividad.promedioProgreso(actividades)
        assertEquals(50.0, promedio, 0.01)
    }

    @Test
    fun `promedioProgreso devuelve 0 cuando la lista esta vacia`() {
        val promedio = ReglasActividad.promedioProgreso(emptyList())
        assertEquals(0.0, promedio, 0.01)
    }

    @Test
    fun `buscarPorTitulo es insensible a mayusculas`() {
        val actividades = listOf(ActividadFormativa(1, "Kotlin", null, 0, 5, EstadoActividad.PENDIENTE, Prioridad.BAJA))
        val resultado = ReglasActividad.buscarPorTitulo(actividades, "KOT")
        assertEquals(1, resultado.size)
    }

    @Test
    fun `actividadesUrgentes detecta tareas con menos de 24 horas (HU14)`() {
        val actividades = listOf(
            ActividadFormativa(1, "Urgente", null, 0, 0, EstadoActividad.PENDIENTE, Prioridad.ALTA), // 0 días = Urgente
            ActividadFormativa(2, "No Urgente", null, 0, 5, EstadoActividad.PENDIENTE, Prioridad.MEDIA),
        )
        val urgentes = ReglasActividad.actividadesUrgentes(actividades)
        assertEquals(1, urgentes.size)
        assertEquals("Urgente", urgentes[0].titulo)
    }
}

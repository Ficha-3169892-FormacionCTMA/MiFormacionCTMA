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
    fun `obtenerEstado devuelve COMPLETADA cuando progreso es 100`() {
        val estado = ReglasActividad.obtenerEstado(100, 5)
        assertEquals(EstadoActividad.COMPLETADA, estado)
    }

    @Test
    fun `obtenerEstado devuelve VENCIDA cuando diasRestantes es negativo`() {
        val estado = ReglasActividad.obtenerEstado(50, -1)
        assertEquals(EstadoActividad.VENCIDA, estado)
    }

    @Test
    fun `promedioProgreso calcula correctamente el promedio (HU16)`() {
        val actividades = listOf(
            ActividadFormativa(1, "A", null, 40, 5, Prioridad.ALTA, 10),
            ActividadFormativa(2, "B", null, 60, 5, Prioridad.MEDIA, 20),
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
        val actividades = listOf(ActividadFormativa(1, "Kotlin", null, 0, 5, Prioridad.BAJA))
        val resultado = ReglasActividad.buscarPorTitulo(actividades, "KOT")
        assertEquals(1, resultado.size)
    }

    @Test
    fun `actividadesUrgentes detecta tareas con menos de 24 horas (HU14)`() {
        val actividades = listOf(
            ActividadFormativa(1, "Urgente", null, 0, 0, Prioridad.ALTA), // 0 días = Urgente
            ActividadFormativa(2, "No Urgente", null, 0, 5, Prioridad.MEDIA),
        )
        val urgentes = ReglasActividad.actividadesUrgentes(actividades)
        assertEquals(1, urgentes.size)
        assertEquals("Urgente", urgentes[0].titulo)
    }
}

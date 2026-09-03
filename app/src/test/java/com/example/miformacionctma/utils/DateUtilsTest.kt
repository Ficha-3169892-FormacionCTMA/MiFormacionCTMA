package com.example.miformacionctma.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class DateUtilsTest {

    @Test
    fun `Seleccion de Fecha Limite - Caso 1 Seleccion de fecha valida`() {
        // Given: El usuario elige una fecha futura (ej. 31/12/2025)
        val date = LocalDate.of(2025, 12, 31)
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        // When: Se formatea para mostrar en el campo
        val resultado = DateUtils.formatToDisplay(millis)
        
        // Then: El campo muestra la fecha seleccionada (DD/MM/AAAA)
        assertEquals("31/12/2025", resultado)
    }

    @Test
    fun `Seleccion de Fecha Limite - Caso 2 Intento de seleccionar fecha pasada`() {
        // Given: Una fecha pasada (ayer)
        val ayer = LocalDate.now(ZoneId.systemDefault()).minusDays(1)
        val millis = ayer.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        // When: El sistema valida la fecha
        val esPasada = DateUtils.isFechaPasada(millis)
        
        // Then: El sistema bloquea la seleccion (identifica que es pasada)
        assertTrue("El sistema debe identificar la fecha como pasada para bloquearla", esPasada)
    }

    @Test
    fun `formatToDisplay returns empty string when null`() {
        assertEquals("", DateUtils.formatToDisplay(null))
    }

    @Test
    fun `formatToBackend returns correct format AAAA-MM-DD`() {
        val date = LocalDate.of(2023, 12, 25)
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals("2023-12-25", DateUtils.formatToBackend(millis))
    }

    @Test
    fun `calcularDiasRestantes returns correct days for future date`() {
        val hoy = LocalDate.now(ZoneId.systemDefault())
        val futuro = hoy.plusDays(5)
        val millis = futuro.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(5, DateUtils.calcularDiasRestantes(millis))
    }

    @Test
    fun `calcularDiasRestantes returns 0 for past date`() {
        val hoy = LocalDate.now(ZoneId.systemDefault())
        val pasado = hoy.minusDays(2)
        val millis = pasado.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals(0, DateUtils.calcularDiasRestantes(millis))
    }
}

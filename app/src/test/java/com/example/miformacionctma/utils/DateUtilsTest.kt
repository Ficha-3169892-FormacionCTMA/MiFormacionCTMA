package com.example.miformacionctma.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateUtilsTest {

    @Test
    fun `formatToDisplay returns empty string when null`() {
        assertEquals("", DateUtils.formatToDisplay(null))
    }

    @Test
    fun `formatToDisplay returns correct format DD-MM-AAAA`() {
        // 2023-12-25
        val date = LocalDate.of(2023, 12, 25)
        val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertEquals("25/12/2023", DateUtils.formatToDisplay(millis))
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

    @Test
    fun `isFechaPasada returns true for yesterday`() {
        val ayer = LocalDate.now(ZoneId.systemDefault()).minusDays(1)
        val millis = ayer.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        assertTrue(DateUtils.isFechaPasada(millis))
    }
}

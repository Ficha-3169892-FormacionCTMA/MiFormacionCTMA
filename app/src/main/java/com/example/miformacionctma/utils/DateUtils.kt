package com.example.miformacionctma.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    private val backendFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun formatToDisplay(millis: Long?): String {
        if (millis == null) return ""
        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(displayFormatter)
    }

    fun formatToBackend(millis: Long?): String {
        if (millis == null) return ""
        val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return localDate.format(backendFormatter)
    }

    fun calcularDiasRestantes(millis: Long?): Int {
        if (millis == null) return 0
        val hoy = LocalDate.now(ZoneId.systemDefault())
        val fechaLimite = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return ChronoUnit.DAYS.between(hoy, fechaLimite).toInt().coerceAtLeast(0)
    }

    fun isFechaPasada(millis: Long): Boolean {
        val hoy = LocalDate.now(ZoneId.systemDefault())
        val fecha = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return fecha.isBefore(hoy)
    }
}

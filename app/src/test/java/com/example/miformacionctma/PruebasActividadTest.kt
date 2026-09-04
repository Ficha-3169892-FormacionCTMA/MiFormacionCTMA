package com.example.miformacionctma

import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import org.junit.Assert.assertEquals
import org.junit.Test

class PruebasActividadTest {

    @Test
    fun `debe calcular correctamente el promedio de progreso de las actividades`() {
        // 1. GIVEN (Dado): Una lista de actividades de prueba
        val actividad1 = ActividadFormativa(
            id = 1,
            titulo = "Diseño de Caso de Uso",
            descripcion = "Modelado UML",
            progreso = 100,
            prioridad = Prioridad.ALTA,
            diasRestantes = 0,
        )

        val actividad2 = ActividadFormativa(
            id = 2,
            titulo = "Construccion de API",
            descripcion = "Endpoints en FastAPI",
            progreso = 50,
            prioridad = Prioridad.MEDIA,
            diasRestantes = 3,
        )

        val lista = listOf(actividad1, actividad2)

        // 2. WHEN (Cuando): Ejecutamos tu función real 'promedioProgreso'
        val promedioCalculado = ReglasActividad.promedioProgreso(lista)

        // 3. THEN (Entonces): Verificamos que el promedio de (100 + 50) / 2 sea 75.0
        assertEquals(75.0, promedioCalculado, 0.01)
    }
}
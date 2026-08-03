package com.example.miformacionctma.domain

object ReglasActividad {

    // 1. Validar actividad (devuelve TODOS los errores encontrados)
    fun validarActividad(titulo: String, progreso: Int): List<String> = buildList {
        if (titulo.isBlank()) {
            add("El título no puede estar vacío.")
        }
        if (progreso !in 0..100) {
            add("El progreso debe estar entre 0 y 100.")
        }
    }

    // 2. Determinar estado de la actividad
    fun obtenerEstado(progreso: Int, diasRestantes: Int): EstadoActividad {
        return when {
            progreso == 100 -> EstadoActividad.COMPLETADA
            diasRestantes < 0 -> EstadoActividad.VENCIDA
            progreso > 0 -> EstadoActividad.EN_PROCESO
            else -> EstadoActividad.PENDIENTE
        }
    }

    // 3. Actividades urgentes (no completadas con 2 días o menos)
    fun actividadesUrgentes(actividades: List<ActividadFormativa>): List<ActividadFormativa> {
        return actividades.filter { actividad ->
            val estado = obtenerEstado(actividad.progreso, actividad.diasRestantes)
            estado != EstadoActividad.COMPLETADA && actividad.diasRestantes <= 2
        }
    }

    // 4. Promedio de progreso (manejo de lista vacía sin división por cero)
    fun promedioProgreso(actividades: List<ActividadFormativa>): Double {
        if (actividades.isEmpty()) return 0.0
        val progresosValidos = actividades.map { it.progreso }.filter { it in 0..100 }
        if (progresosValidos.isEmpty()) return 0.0
        return progresosValidos.average()
    }

    // 5. Búsqueda por título (ignora mayúsculas/minúsculas y espacios externos)
    fun buscarPorTitulo(actividades: List<ActividadFormativa>, textoBusqueda: String): List<ActividadFormativa> {
        val consultaLimpia = textoBusqueda.trim()
        if (consultaLimpia.isEmpty()) return actividades

        return actividades.filter { actividad ->
            actividad.titulo.contains(consultaLimpia, ignoreCase = true)
        }
    }

    // Reto Adicional: Ordenar actividades (Vencidas primero, luego prioridad alta, luego menor días)
    fun ordenarActividades(actividades: List<ActividadFormativa>): List<ActividadFormativa> {
        return actividades.sortedWith(
            compareByDescending<ActividadFormativa> { obtenerEstado(it.progreso, it.diasRestantes) == EstadoActividad.VENCIDA }
                .thenByDescending { it.prioridad }
                .thenBy { it.diasRestantes }
        )
    }
}
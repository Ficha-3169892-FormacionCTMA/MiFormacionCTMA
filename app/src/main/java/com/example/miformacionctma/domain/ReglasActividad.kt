package com.example.miformacionctma.domain

object ReglasActividad {

    // 1. Validaciones individuales para el formulario (Usadas en CrearScreen)
    fun validarTitulo(valor: String, mostrarVacio: Boolean): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() && mostrarVacio -> "Escribe un título para la actividad"
            limpio.isNotEmpty() && limpio.length < 3 -> "Usa al menos 3 caracteres"
            limpio.length > 80 -> "Usa máximo 80 caracteres"
            else -> null
        }
    }

    fun validarDescripcion(valor: String): String? {
        return if (valor.length > 240) "Usa máximo 240 caracteres" else null
    }

    fun validarFecha(fecha: Long?, mostrarVacio: Boolean): String? {
        return if (fecha == null && mostrarVacio) "Selecciona una fecha límite" else null
    }

    // 2. Validación integral
    fun validarActividad(actividad: ActividadFormativa): List<String> = buildList {
        if (actividad.titulo.isBlank()) add("El título no puede estar vacío.")
        if (actividad.progreso !in 0..100) add("El progreso debe estar entre 0 y 100.")
    }

    // 3. Determinar estado de la actividad
    fun obtenerEstado(progreso: Int, diasRestantes: Int): EstadoActividad {
        return when {
            progreso >= 100 -> EstadoActividad.COMPLETADA
            diasRestantes < 0 -> EstadoActividad.VENCIDA
            progreso > 0 -> EstadoActividad.EN_PROCESO
            else -> EstadoActividad.PENDIENTE
        }
    }

    // 4. Actividades urgentes (no completadas con 2 días o menos)
    fun actividadesUrgentes(actividades: List<ActividadFormativa>): List<ActividadFormativa> {
        return actividades.filter { actividad ->
            val estado = obtenerEstado(actividad.progreso, actividad.diasRestantes)
            estado != EstadoActividad.COMPLETADA && actividad.diasRestantes <= 2
        }
    }

    // 5. Promedio de progreso
    fun promedioProgreso(actividades: List<ActividadFormativa>): Double {
        if (actividades.isEmpty()) return 0.0
        return actividades.map { it.progreso }.average().coerceIn(0.0, 100.0)
    }

    // 6. Búsqueda por título
    fun buscarPorTitulo(actividades: List<ActividadFormativa>, textoBusqueda: String): List<ActividadFormativa> {
        val consultaLimpia = textoBusqueda.trim()
        if (consultaLimpia.isEmpty()) return actividades

        return actividades.filter { actividad ->
            actividad.titulo.contains(consultaLimpia, ignoreCase = true)
        }
    }

    // 7. Ordenar actividades (Vencidas primero, luego urgentes, luego prioridad, luego menor días)
    fun ordenarActividades(actividades: List<ActividadFormativa>): List<ActividadFormativa> {
        return actividades.sortedWith(
            compareByDescending<ActividadFormativa> { obtenerEstado(it.progreso, it.diasRestantes) == EstadoActividad.VENCIDA }
                .thenByDescending { actividadesUrgentes(listOf(it)).isNotEmpty() }
                .thenByDescending { it.prioridad }
                .thenBy { it.diasRestantes }
        )
    }
}

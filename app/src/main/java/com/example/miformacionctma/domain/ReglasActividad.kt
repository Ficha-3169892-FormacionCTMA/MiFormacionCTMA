@file:Suppress("unused", "SpellCheckingInspection")

package com.example.miformacionctma.domain

object ReglasActividad {

    // 1. Validar actividad (devuelve TODOS los errores encontrados)
    @Suppress("unused")
    fun validarActividad(titulo: String, progreso: Int): List<String> = buildList {
        if (titulo.isBlank()) {
            add("El título no puede estar vacío.")
        }
        if (progreso !in (0..100)) {
            add("El progreso debe estar entre 0 y 100.")
        }
    }

    // 2. Validar título individualmente (Semana 4)
    fun validarTitulo(valor: String, mostrarVacio: Boolean): String? {
        val limpio = valor.trim()
        return when {
            limpio.isEmpty() && mostrarVacio -> "Escribe un título"
            limpio.isNotEmpty() && limpio.length < 3 -> "Usa al menos 3 caracteres"
            limpio.length > 80 -> "Usa máximo 80 caracteres"
            else -> null
        }
    }

    // 2b. Validar descripción (Semana 4)
    fun validarDescripcion(valor: String): String? {
        return if (valor.length > 240) "Máximo 240 caracteres" else null
    }

    // 3. Validar fecha (Semana 4 - no anterior a hoy)
    fun validarFecha(fechaMillis: Long?): String? {
        if (fechaMillis == null) return "La fecha es obligatoria"
        
        val hoy = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        return if (fechaMillis < hoy) "La fecha no puede ser anterior a hoy" else null
    }

    // 4. Determinar estado de la actividad
    fun obtenerEstado(progreso: Int, diasRestantes: Int): EstadoActividad {
        return when {
            progreso == 100 -> EstadoActividad.COMPLETADA
            diasRestantes < 0 -> EstadoActividad.VENCIDA
            progreso == 0 -> EstadoActividad.PENDIENTE
            else -> EstadoActividad.EN_PROGRESO
        }
    }

    // 5. Actividades urgentes (no completadas con 1 día o menos - HU14)
    fun actividadesUrgentes(actividades: List<ActividadFormativa>): List<ActividadFormativa> {
        return actividades.filter { actividad ->
            val estado = obtenerEstado(actividad.progreso, actividad.diasRestantes)
            (estado != EstadoActividad.COMPLETADA) && (actividad.diasRestantes <= 1)
        }
    }

    // 6. Promedio de progreso (manejo de lista vacía sin división por cero)
    fun promedioProgreso(actividades: List<ActividadFormativa>): Double {
        if (actividades.isEmpty()) return 0.0
        val progresosValidos = actividades.asSequence()
            .map { it.progreso }
            .filter { it in (0..100) }
            .toList()
        if (progresosValidos.isEmpty()) return 0.0
        return progresosValidos.average()
    }

    // 7. Búsqueda por título (ignora mayúsculas/minúsculas y espacios externos)
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
            compareByDescending<ActividadFormativa> {
                obtenerEstado(it.progreso, it.diasRestantes) == EstadoActividad.VENCIDA
            }
                .thenByDescending { it.prioridad }
                .thenBy { it.diasRestantes },
        )
    }
}

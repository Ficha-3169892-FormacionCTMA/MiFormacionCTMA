package com.example.miformacionctma.ui.screens

import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad

data class FormularioActividadUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val progreso: Int = 0,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val fechaSeleccionadaMillis: Long? = null,
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val fechaError: String? = null,
    val intentoGuardar: Boolean = false
) {
    val puedeGuardar: Boolean
        get() = ReglasActividad.validarTitulo(titulo, true) == null &&
                ReglasActividad.validarDescripcion(descripcion) == null &&
                fechaSeleccionadaMillis != null &&
                ReglasActividad.validarFecha(fechaSeleccionadaMillis) == null
}

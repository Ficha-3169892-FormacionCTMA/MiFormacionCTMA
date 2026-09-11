package com.example.miformacionctma.ui.states

import com.example.miformacionctma.domain.ActividadFormativa

/**
 * Representa los estados posibles de la pantalla de listado de actividades.
 */
sealed interface ListadoUiState {
    data object Cargando : ListadoUiState
    data object Vacio : ListadoUiState
    data class Contenido(val actividades: List<ActividadFormativa>) : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

/**
 * Representa los estados de una operación de escritura (Guardar/Eliminar).
 */
sealed interface OperacionUiState {
    data object Inactiva : OperacionUiState
    data object EnCurso : OperacionUiState
    data object Exitosa : OperacionUiState
    data class Fallida(val mensaje: String) : OperacionUiState
}

package com.example.miformacionctma.ui

import androidx.lifecycle.ViewModel
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import java.util.concurrent.TimeUnit

data class ActividadesUiState(
    val actividades: List<Actividad> = emptyList(),
    val actividadSeleccionada: Actividad? = null
)

class ActividadesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadesUiState())
    val uiState: StateFlow<ActividadesUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ActividadesUiState(actividades = MockData.obtenerActividades())
    }

    fun seleccionarActividad(id: Int) {
        val actividad = _uiState.value.actividades.find { it.id == id }
        _uiState.value = _uiState.value.copy(actividadSeleccionada = actividad)
    }

    fun actualizarProgresoActividad(id: Int, nuevoProgreso: Float) {
        val listaActualizada = _uiState.value.actividades.map { actividad ->
            if (actividad.id == id) actividad.copy(progreso = nuevoProgreso) else actividad
        }
        val seleccionadaActualizada = _uiState.value.actividadSeleccionada?.let {
            if (it.id == id) it.copy(progreso = nuevoProgreso) else it
        }
        _uiState.value = _uiState.value.copy(
            actividades = listaActualizada,
            actividadSeleccionada = seleccionadaActualizada
        )
    }

    // Nueva función para Semana 4
    fun agregarActividad(titulo: String, descripcion: String) {
        val listaActual = _uiState.value.actividades
        val nuevoId = (listaActual.maxOfOrNull { it.id } ?: 0) + 1
        val nuevaActividad = Actividad(
            id = nuevoId,
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            fechaLimite = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)),
            progreso = 0.0f
        )
        _uiState.value = _uiState.value.copy(actividades = listaActual + nuevaActividad)
    }
}
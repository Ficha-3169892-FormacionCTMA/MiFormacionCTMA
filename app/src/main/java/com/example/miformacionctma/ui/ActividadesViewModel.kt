package com.example.miformacionctma.ui

import androidx.lifecycle.ViewModel
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ActividadesUiState(
    val actividades: List<Actividad> = emptyList(),
    val actividadSeleccionada: Actividad? = null
)

class ActividadesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadesUiState())
    val uiState: StateFlow<ActividadesUiState> = _uiState.asStateFlow()

    init {
        cargarActividades()
    }

    private fun cargarActividades() {
        _uiState.value = ActividadesUiState(
            actividades = MockData.obtenerActividades()
        )
    }

    fun seleccionarActividad(id: Int) {
        val encontrada = _uiState.value.actividades.find { it.id == id }
        _uiState.value = _uiState.value.copy(actividadSeleccionada = encontrada)
    }

    fun alternarEstadoActividad(id: Int) {
        val listaActualizada = _uiState.value.actividades.map { actividad ->
            if (actividad.id == id) {
                actividad.copy(completada = !actividad.completada)
            } else {
                actividad
            }
        }
        val seleccionadaActualizada = _uiState.value.actividadSeleccionada?.let { actual ->
            if (actual.id == id) actual.copy(completada = !actual.completada) else actual
        }

        _uiState.value = _uiState.value.copy(
            actividades = listaActualizada,
            actividadSeleccionada = seleccionadaActualizada
        )
    }
}
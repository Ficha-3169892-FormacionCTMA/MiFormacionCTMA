package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miformacionctma.domain.ActividadFormativa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Representa las respuestas de la UI tras intentar actualizar
sealed class EstadoProgresoUI {
    object Reposo : EstadoProgresoUI()
    data class Exito(val mensaje: String) : EstadoProgresoUI()
    data class Error(val mensaje: String) : EstadoProgresoUI()
}

class ActividadesViewModel : ViewModel() {

    private val _estadoUI = MutableStateFlow<EstadoProgresoUI>(EstadoProgresoUI.Reposo)
    val estadoUI: StateFlow<EstadoProgresoUI> = _estadoUI.asStateFlow()

    /**
     * Valida y actualiza el progreso según la regla de negocio
     */
    fun actualizarProgreso(actividad: ActividadFormativa, nuevoProgreso: Int) {
        // Regla 1: Verificar si la actividad está vencida (diasRestantes <= 0)
        if (actividad.diasRestantes <= 0) {
            _estadoUI.value = EstadoProgresoUI.Error("No se puede editar: la actividad está vencida.")
            return
        }

        // Regla 2: Verificar rango del porcentaje (0 a 100)
        if (nuevoProgreso !in (0..100)) {
            _estadoUI.value = EstadoProgresoUI.Error("Porcentaje inválido ($nuevoProgreso%). Debe estar entre 0 y 100.")
            return
        }

        // Si pasa las validaciones exitosamente:
        _estadoUI.value = EstadoProgresoUI.Exito("Progreso actualizado correctamente a $nuevoProgreso%")
    }

    fun reiniciarEstado() {
        _estadoUI.value = EstadoProgresoUI.Reposo
    }
}
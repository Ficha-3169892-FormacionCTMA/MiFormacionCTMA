package com.example.miformacionctma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.miformacionctma.FormacionApplication
import com.example.miformacionctma.data.Actividad
import com.example.miformacionctma.data.ActividadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

data class ActividadesUiState(
    val actividades: List<Actividad> = emptyList(),
    val actividadSeleccionada: Actividad? = null,
    val mostrarFinalizadas: Boolean = false
)

class ActividadesViewModel(private val repository: ActividadRepository) : ViewModel() {

    private val _mostrarFinalizadas = MutableStateFlow(false)
    private val _actividadSeleccionadaId = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ActividadesUiState> = combine(
        repository.allActividades,
        _mostrarFinalizadas,
        _actividadSeleccionadaId
    ) { actividades, mostrarFinalizadas, seleccionadaId ->
        val filtradas = if (mostrarFinalizadas) {
            actividades.filter { it.progreso >= 1.0f || it.completada }
        } else {
            actividades.filter { it.progreso < 1.0f && !it.completada }
        }
        
        ActividadesUiState(
            actividades = filtradas,
            actividadSeleccionada = actividades.find { it.id == seleccionadaId },
            mostrarFinalizadas = mostrarFinalizadas
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActividadesUiState()
    )

    fun seleccionarActividad(id: Int?) {
        _actividadSeleccionadaId.value = id
    }

    fun toggleMostrarFinalizadas() {
        _mostrarFinalizadas.value = !_mostrarFinalizadas.value
    }

    fun actualizarProgresoActividad(id: Int, nuevoProgreso: Float) {
        viewModelScope.launch {
            val actividad = uiState.value.actividades.find { it.id == id } 
                ?: repository.allActividades.stateIn(viewModelScope).value.find { it.id == id }
            actividad?.let {
                repository.updateActividad(it.copy(
                    progreso = nuevoProgreso,
                    completada = nuevoProgreso >= 1.0f
                ))
            }
        }
    }

    fun agregarActividad(titulo: String, descripcion: String) {
        viewModelScope.launch {
            val nuevaActividad = Actividad(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                fechaLimite = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)),
                progreso = 0.0f
            )
            repository.insertActividad(nuevaActividad)
        }
    }

    fun editarActividad(id: Int, titulo: String, descripcion: String) {
        viewModelScope.launch {
            val actividadActual = repository.getActividadById(id)
            actividadActual?.let {
                repository.updateActividad(it.copy(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim()
                ))
            }
        }
    }

    fun eliminarActividad(actividad: Actividad) {
        viewModelScope.launch {
            repository.deleteActividad(actividad)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as FormacionApplication
                return ActividadesViewModel(application.repository) as T
            }
        }
    }
}

package com.example.miformacionctma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.miformacionctma.ActividadesApplication
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActividadesUiState(
    val actividadesVisibles: List<ActividadFormativa> = emptyList(),
    val searchQuery: String = "",
    val preferencias: PreferenciasUsuario = PreferenciasUsuario(),
    val actividadSeleccionada: ActividadFormativa? = null,
)

class ActividadesViewModel(
    private val actividadRepository: ActividadRepository,
    private val preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _actividadSeleccionadaId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<ActividadesUiState> = combine(
        actividadRepository.observarTodos(),
        _searchQuery,
        preferenciasRepository.preferencias,
        _actividadSeleccionadaId,
    ) { lista, query, prefs, seleccionadaId ->
        
        val filtradas = lista.asSequence()
            .filter { it.titulo.contains(query, ignoreCase = true) }
            .filter { (prefs.filtroPrioridad == null) || (it.prioridad == prefs.filtroPrioridad) }
            .toList()
            .let { 
                if (prefs.ordenadoPorVencimiento) it.sortedBy { a -> a.diasRestantes } else it 
            }

        val seleccionada = if (seleccionadaId != null) lista.find { it.id == seleccionadaId } else null

        ActividadesUiState(
            actividadesVisibles = filtradas,
            searchQuery = query,
            preferencias = prefs,
            actividadSeleccionada = seleccionada,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActividadesUiState(),
    )

    fun buscar(query: String) {
        _searchQuery.value = query
    }

    fun filtrarPorPrioridad(prioridad: Prioridad?) {
        viewModelScope.launch {
            preferenciasRepository.guardarFiltroPrioridad(prioridad)
        }
    }

    fun alternarOrden() {
        viewModelScope.launch {
            val actual = uiState.value.preferencias.ordenadoPorVencimiento
            preferenciasRepository.guardarOrdenadoPorVencimiento(!actual)
        }
    }

    fun guardarActividad(
        titulo: String, 
        descripcion: String, 
        progreso: Int, 
        prioridad: Prioridad, 
        fechaMillis: Long,
    ) {
        viewModelScope.launch {
            val hoy = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val diasRestantes = ((fechaMillis - hoy) / (1000 * 60 * 60 * 24)).toInt()

            val nuevaActividad = ActividadFormativa(
                id = 0, // Room generará el ID automáticamente
                titulo = titulo,
                descripcion = descripcion,
                progreso = progreso,
                prioridad = prioridad,
                diasRestantes = diasRestantes,
                estado = com.example.miformacionctma.domain.ReglasActividad.obtenerEstado(progreso, diasRestantes),
            )
            actividadRepository.guardar(nuevaActividad)
        }
    }

    fun seleccionarActividad(id: Long) {
        _actividadSeleccionadaId.value = id
    }

    @Suppress("unused")
    fun alternarEstadoActividad(id: Long) {
        viewModelScope.launch {
            val actividad = uiState.value.actividadesVisibles.find { it.id == id }
            actividad?.let {
                val nuevoProgreso = if (it.progreso == 100) 0 else 100
                actividadRepository.guardar(it.copy(progreso = nuevoProgreso))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as ActividadesApplication
                return ActividadesViewModel(
                    application.actividadRepository,
                    application.preferenciasRepository,
                ) as T
            }
        }
    }
}

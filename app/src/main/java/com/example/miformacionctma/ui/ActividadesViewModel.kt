package com.example.miformacionctma.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.MockData
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import kotlinx.coroutines.flow.*

data class ActividadesUiState(
    val actividadesVisibles: List<ActividadFormativa> = emptyList(),
    val searchQuery: String = "",
    val filtroPrioridad: Prioridad? = null,
    val ordenadoPorVencimiento: Boolean = false,
    val actividadSeleccionada: ActividadFormativa? = null,
)

class ActividadesViewModel : ViewModel() {

    private val _actividadesOriginales = MutableStateFlow<List<ActividadFormativa>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _filtroPrioridad = MutableStateFlow<Prioridad?>(null)
    private val _ordenadoPorVencimiento = MutableStateFlow(value = false)
    private val _actividadSeleccionada = MutableStateFlow<ActividadFormativa?>(null)

    val uiState: StateFlow<ActividadesUiState> = combine(
        _actividadesOriginales,
        _searchQuery,
        _filtroPrioridad,
        _ordenadoPorVencimiento,
        _actividadSeleccionada,
    ) { lista, query, prioridad, ordenado, seleccionada ->
        
        val filtradas = lista.asSequence()
            .filter { it.titulo.contains(query, ignoreCase = true) }
            .filter { (prioridad == null) || (it.prioridad == prioridad) }
            .toList()
            .let { 
                if (ordenado) it.sortedBy { a -> a.diasRestantes } else it 
            }

        ActividadesUiState(
            actividadesVisibles = filtradas,
            searchQuery = query,
            filtroPrioridad = prioridad,
            ordenadoPorVencimiento = ordenado,
            actividadSeleccionada = seleccionada
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActividadesUiState(),
    )

    init {
        _actividadesOriginales.value = MockData.listaActividades
    }

    fun buscar(query: String) {
        _searchQuery.value = query
    }

    fun filtrarPorPrioridad(prioridad: Prioridad?) {
        _filtroPrioridad.value = prioridad
    }

    fun alternarOrden() {
        _ordenadoPorVencimiento.value = !_ordenadoPorVencimiento.value
    }

    fun guardarActividad(
        titulo: String, 
        descripcion: String, 
        progreso: Int, 
        prioridad: Prioridad, 
        fechaMillis: Long,
    ) {
        val hoy = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val diasRestantes = ((fechaMillis - hoy) / (1000 * 60 * 60 * 24)).toInt()

        val nuevaActividad = ActividadFormativa(
            id = (_actividadesOriginales.value.maxOfOrNull { it.id } ?: 0L) + 1L,
            titulo = titulo,
            descripcion = descripcion,
            progreso = progreso,
            prioridad = prioridad,
            diasRestantes = diasRestantes,
            estado = ReglasActividad.obtenerEstado(progreso, diasRestantes),
        )
        
        _actividadesOriginales.update { it + nuevaActividad }
    }

    fun seleccionarActividad(id: Long) {
        val encontrada = _actividadesOriginales.value.find { it.id == id }
        _actividadSeleccionada.value = encontrada
    }

    @Suppress("unused")
    fun alternarEstadoActividad(id: Long) {
        _actividadesOriginales.update { lista ->
            lista.map { actividad ->
                if (actividad.id == id) {
                    val nuevoProgreso = if (actividad.progreso == 100) 0 else 100
                    actividad.copy(
                        progreso = nuevoProgreso,
                        estado = ReglasActividad.obtenerEstado(nuevoProgreso, actividad.diasRestantes)
                    )
                } else {
                    actividad
                }
            }
        }
        
        // Actualizar también la seleccionada si coincide
        if (_actividadSeleccionada.value?.id == id) {
            val actual = _actividadSeleccionada.value!!
            val nuevoProgreso = if (actual.progreso == 100) 0 else 100
            _actividadSeleccionada.value = actual.copy(
                progreso = nuevoProgreso,
                estado = ReglasActividad.obtenerEstado(nuevoProgreso, actual.diasRestantes)
            )
        }
    }
}

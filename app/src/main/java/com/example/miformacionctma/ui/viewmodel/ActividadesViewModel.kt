package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.miformacionctma.ActividadesApplication
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.states.ListadoUiState
import com.example.miformacionctma.ui.states.OperacionUiState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModel(
    private val actividadRepository: ActividadRepository,
    private val preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _actividadSeleccionadaId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<ListadoUiState> = _searchQuery
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            val flowActividades = if (query.isEmpty()) {
                actividadRepository.observarTodos()
            } else {
                actividadRepository.buscar(query)
            }

            combine(
                flowActividades,
                preferenciasRepository.preferencias,
            ) { lista, prefs ->
                lista.asSequence()
                    .filter { (prefs.filtroPrioridad == null) || (it.prioridad == prefs.filtroPrioridad) }
                    .toList()
                    .let { 
                        if (prefs.ordenadoPorVencimiento) it.sortedBy { a -> a.diasRestantes } else it 
                    }
            }
        }
        .map { lista ->
            if (lista.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(lista)
        }
        .catch { error ->
            if (error is CancellationException) throw error
            emit(ListadoUiState.Error(error.message ?: "Error desconocido"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListadoUiState.Cargando,
        )

    private val _operacion = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacion: StateFlow<OperacionUiState> = _operacion.asStateFlow()

    val preferencias = preferenciasRepository.preferencias
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.miformacionctma.domain.PreferenciasUsuario())

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
            val currentPrefs = preferencias.value
            preferenciasRepository.guardarOrdenadoPorVencimiento(!currentPrefs.ordenadoPorVencimiento)
        }
    }

    fun guardarActividad(
        id: Long = 0,
        titulo: String, 
        descripcion: String, 
        progreso: Int, 
        prioridad: Prioridad, 
        fechaMillis: Long,
        horas: Int = 10,
        enlaceEvidencia: String? = null
    ) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                val hoy = LocalDate.now()
                val fechaObjetivo = Instant.ofEpochMilli(fechaMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                
                val diasRestantes = ChronoUnit.DAYS.between(hoy, fechaObjetivo).toInt()

                val nuevaActividad = ActividadFormativa(
                    id = id,
                    titulo = titulo,
                    descripcion = descripcion,
                    progreso = progreso,
                    prioridad = prioridad,
                    diasRestantes = diasRestantes,
                    estado = ReglasActividad.obtenerEstado(progreso, diasRestantes),
                    horas = horas,
                    enlaceEvidencia = enlaceEvidencia
                )
                actividadRepository.guardar(nuevaActividad)
                _operacion.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al guardar")
            }
        }
    }

    fun resetOperacion() {
        _operacion.value = OperacionUiState.Inactiva
    }

    fun seleccionarActividad(id: Long?) {
        _actividadSeleccionadaId.value = id
    }

    fun eliminarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                actividadRepository.eliminar(actividad.id)
                _operacion.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al eliminar")
            }
        }
    }

    fun restaurarActividad(actividad: ActividadFormativa) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                actividadRepository.guardar(actividad)
                _operacion.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al restaurar")
            }
        }
    }

    /**
     * Actualiza solo el progreso, preservando el resto de campos (incluyendo evidencias).
     */
    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                actividadRepository.actualizarProgreso(id, nuevoProgreso)
                _operacion.value = OperacionUiState.Exitosa
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al actualizar")
            }
        }
    }

    fun actualizarRecordatorios(activos: Boolean) {
        viewModelScope.launch {
            preferenciasRepository.guardarRecordatoriosActivos(activos)
        }
    }

    val actividadSeleccionada: StateFlow<ActividadFormativa?> = _actividadSeleccionadaId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else actividadRepository.observarPorId(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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

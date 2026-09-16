package com.example.miformacionctma.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.miformacionctma.ActividadesApplication
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import com.example.miformacionctma.ui.states.ListadoUiState
import com.example.miformacionctma.ui.states.OperacionUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadesViewModel(
    private val actividadRepository: ActividadRepository,
    private val evidenciaRepository: EvidenciaRepository,
    private val preferenciasRepository: PreferenciasRepository,
) : ViewModel() {

    // [HU 05] Búsqueda en Tiempo Real
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _actividadSeleccionadaId = MutableStateFlow<Long?>(null)

    // [HU 07] Filtrado por Nivel de Prioridad & [HU 08] Ordenación por Fecha de Vencimiento
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

    // [HU 01] Persistencia con Room Database
    fun guardarActividad(
        titulo: String, 
        descripcion: String, 
        progreso: Int, 
        prioridad: Prioridad, 
        fechaMillis: Long,
    ) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
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
                    estado = ReglasActividad.obtenerEstado(progreso, diasRestantes),
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

    // [HU 13] Apertura de Enlaces de Evidencia & [HU 15] Compartir Resumen de Formación
    fun seleccionarActividad(id: Long) {
        _actividadSeleccionadaId.value = id
    }

    // [HU 02] Eliminación de Actividades (Swipe-to-Dismiss)
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

    // [HU 03] Edición de Actividades Existentes & [HU 11] Control de Progreso Granular
    fun actualizarProgreso(id: Long, nuevoProgreso: Int) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                val flow = actividadRepository.observarPorId(id).first()
                flow?.let {
                    val actividadActualizada = it.copy(
                        progreso = nuevoProgreso,
                        estado = ReglasActividad.obtenerEstado(nuevoProgreso, it.diasRestantes),
                    )
                    actividadRepository.guardar(actividadActualizada)
                    _operacion.value = OperacionUiState.Exitosa
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al actualizar")
            }
        }
    }

    val actividadSeleccionada: StateFlow<ActividadFormativa?> = _actividadSeleccionadaId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else actividadRepository.observarPorId(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val evidenciasActividadSeleccionada: StateFlow<List<Evidencia>> = _actividadSeleccionadaId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else evidenciaRepository.observarPorActividad(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // [HU 17] Adjuntar Evidencia (Photo Picker)
    fun adjuntarEvidencia(actividadId: Long, uri: Uri) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            val result = evidenciaRepository.guardarLocal(actividadId, uri)
            result.onSuccess { evidencia ->
                // Intentamos sincronizar inmediatamente
                sincronizarEvidencia(evidencia.id)
            }.onFailure { e ->
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al adjuntar")
            }
        }
    }

    // [HU 18] Almacenamiento en la Nube (Supabase Storage)
    fun sincronizarEvidencia(evidenciaId: String) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            val result = evidenciaRepository.sincronizar(evidenciaId)
            result.onSuccess {
                _operacion.value = OperacionUiState.Exitosa
            }.onFailure { e ->
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error de red")
            }
        }
    }

    fun eliminarEvidencia(evidenciaId: String) {
        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            val result = evidenciaRepository.eliminar(evidenciaId)
            result.onSuccess {
                _operacion.value = OperacionUiState.Exitosa
            }.onFailure { e ->
                _operacion.value = OperacionUiState.Fallida(e.message ?: "Error al eliminar")
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
                    application.evidenciaRepository,
                    application.preferenciasRepository,
                ) as T
            }
        }
    }
}

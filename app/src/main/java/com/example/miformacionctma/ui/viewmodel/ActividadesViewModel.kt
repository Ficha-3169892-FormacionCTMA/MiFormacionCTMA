package com.example.miformacionctma.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.repository.ActividadRepository
import com.example.miformacionctma.ui.ListadoUiState
import com.example.miformacionctma.ui.OperacionUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ActividadesViewModel(
    private val repository: ActividadRepository
) : ViewModel() {

    private val _textoBusqueda = MutableStateFlow("")
    val textoBusqueda: StateFlow<String> = _textoBusqueda.asStateFlow()

    private val _operacion = MutableStateFlow<OperacionUiState>(OperacionUiState.Inactiva)
    val operacion: StateFlow<OperacionUiState> = _operacion.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ListadoUiState> = _textoBusqueda
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.observarActividades()
            } else {
                repository.buscar(query)
            }
        }
        .map { lista ->
            if (lista.isEmpty()) ListadoUiState.Vacio
            else ListadoUiState.Contenido(lista)
        }
        .catch { error ->
            if (error is CancellationException) throw error
            emit(ListadoUiState.Error(error.localizedMessage ?: "Error al cargar datos"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListadoUiState.Cargando
        )

    fun cambiarBusqueda(nuevoTexto: String) {
        _textoBusqueda.value = nuevoTexto
    }

    fun actualizarProgreso(actividad: ActividadFormativa, nuevoProgreso: Int) {
        // Regla 1: Verificar si la actividad está vencida
        if (actividad.diasRestantes <= 0) {
            _operacion.value = OperacionUiState.Fallida("No se puede editar: la actividad está vencida.")
            return
        }

        // Regla 2: Verificar rango del porcentaje (0 a 100)
        if (nuevoProgreso !in (0..100)) {
            _operacion.value = OperacionUiState.Fallida("Porcentaje inválido ($nuevoProgreso%). Debe estar entre 0 y 100.")
            return
        }

        viewModelScope.launch {
            _operacion.value = OperacionUiState.EnCurso
            try {
                repository.guardar(actividad.copy(progreso = nuevoProgreso))
                _operacion.value = OperacionUiState.Exitosa
            } catch (c: CancellationException) {
                throw c
            } catch (e: Exception) {
                _operacion.value = OperacionUiState.Fallida(e.localizedMessage ?: "Error al guardar progreso")
            }
        }
    }

    fun reiniciarEstadoOperacion() {
        _operacion.value = OperacionUiState.Inactiva
    }
}
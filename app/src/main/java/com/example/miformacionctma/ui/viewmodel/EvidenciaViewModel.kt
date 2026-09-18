package com.example.miformacionctma.ui.viewmodel

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.miformacionctma.ActividadesApplication
import com.example.miformacionctma.domain.EvidencePolicy
import com.example.miformacionctma.domain.Evidencia
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.EvidenciaSyncState
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EvidenciaViewModel(
    private val repository: EvidenciaRepository,
    private val contentResolver: ContentResolver
) : ViewModel() {

    private val _errorFeedback = MutableStateFlow<String?>(null)
    val errorFeedback: StateFlow<String?> = _errorFeedback.asStateFlow()

    fun evidenciasPorActividad(actividadId: Long): Flow<List<Evidencia>> =
        repository.observarPorActividad(actividadId)

    fun agregarEvidencia(actividadId: Long, uri: Uri) {
        viewModelScope.launch {
            // Persistir acceso a URIs externas (Galería) - CA-05
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Es normal para URIs que no son persistibles (como las de FileProvider propias)
            }

            val validation = EvidencePolicy.validate(uri, contentResolver)
            if (!validation.isValid) {
                _errorFeedback.value = validation.errorMessage
                return@launch
            }

            val nuevaEvidencia = Evidencia(
                id = UUID.randomUUID().toString(),
                actividadId = actividadId,
                localUri = uri.toString(),
                mimeType = contentResolver.getType(uri) ?: "image/*",
                sizeBytes = contentResolver.openInputStream(uri)?.use { it.available().toLong() } ?: 0L,
                estado = EvidenciaSyncState.LOCAL,
                creadaEnEpochMillis = System.currentTimeMillis()
            )

            repository.guardarLocal(nuevaEvidencia)
            _errorFeedback.value = null
            
            // Intentar subir automáticamente
            repository.subir(nuevaEvidencia.id)
        }
    }

    fun reintentarSubida(evidenciaId: String) {
        viewModelScope.launch {
            repository.subir(evidenciaId)
        }
    }

    fun eliminarEvidencia(evidenciaId: String) {
        viewModelScope.launch {
            repository.eliminar(evidenciaId)
        }
    }

    fun clearError() {
        _errorFeedback.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as ActividadesApplication
                return EvidenciaViewModel(
                    application.evidenciaRepository,
                    application.contentResolver
                ) as T
            }
        }
    }
}

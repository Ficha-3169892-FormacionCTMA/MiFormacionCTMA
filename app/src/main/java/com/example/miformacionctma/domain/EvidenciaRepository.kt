package com.example.miformacionctma.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface EvidenciaRepository {
    fun observarPorActividad(actividadId: Long): Flow<List<Evidencia>>
    suspend fun guardarLocal(actividadId: Long, uri: Uri): Result<Evidencia>
    suspend fun sincronizar(evidenciaId: String): Result<Unit>
    suspend fun eliminar(evidenciaId: String): Result<Unit>
}

package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class RoomActividadRepository(
    private val dao: ActividadDao,
) : ActividadRepository {

    override fun observarTodos(): Flow<List<ActividadFormativa>> =
        dao.observarTodos().map { entities -> entities.map { it.toDomain() } }

    override fun observarPorId(id: Long): Flow<ActividadFormativa?> =
        dao.observarPorId(id).map { it?.toDomain() }

    override fun buscar(texto: String): Flow<List<ActividadFormativa>> =
        dao.buscar(texto).map { entities -> entities.map { it.toDomain() } }

    override suspend fun guardar(actividad: ActividadFormativa) {
        // En un caso real, la competenciaId vendría del modelo de dominio.
        // Aquí usamos 1 como valor por defecto si no existe en el dominio.
        dao.insertar(actividad.toEntity(competenciaId = 1L))
    }

    override suspend fun eliminar(id: Long): Boolean =
        dao.eliminarPorId(id) == 1
}

// Mapeadores
fun ActividadEntity.toDomain(): ActividadFormativa {
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val dias = ((fechaLimiteEpochMillis - hoy) / (1000 * 60 * 60 * 24)).toInt()

    return ActividadFormativa(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        diasRestantes = dias,
        prioridad = Prioridad.valueOf(prioridad),
        estado = ReglasActividad.obtenerEstado(progreso, dias)
    )
}

fun ActividadFormativa.toEntity(competenciaId: Long): ActividadEntity {
    val hoy = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    
    val fechaLimite = hoy + (diasRestantes.toLong() * 1000 * 60 * 60 * 24)

    return ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        prioridad = prioridad.name,
        competenciaId = competenciaId,
        fechaLimiteEpochMillis = fechaLimite,
        completada = progreso >= 100
    )
}

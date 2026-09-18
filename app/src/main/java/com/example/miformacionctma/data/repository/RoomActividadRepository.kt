package com.example.miformacionctma.data.repository

import com.example.miformacionctma.data.local.dao.ActividadDao
import com.example.miformacionctma.data.local.entities.ActividadEntity
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.ActividadRepository
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.ReglasActividad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

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
        // Al usar Upsert en el DAO, si el ID existe se actualiza sin borrar (protegiendo evidencias)
        dao.insertar(actividad.toEntity(competenciaId = 1L))
    }

    override suspend fun eliminar(id: Long): Boolean =
        dao.eliminarPorId(id) == 1

    override suspend fun actualizarProgreso(id: Long, progreso: Int) {
        dao.actualizarProgreso(id, progreso, progreso >= 100)
    }
}

// Mapeadores migrados a java.time para consistencia con el ViewModel
fun ActividadEntity.toDomain(): ActividadFormativa {
    val hoy = LocalDate.now()
    val fechaLimite = Instant.ofEpochMilli(fechaLimiteEpochMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    
    val dias = ChronoUnit.DAYS.between(hoy, fechaLimite).toInt()

    return ActividadFormativa(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        diasRestantes = dias,
        prioridad = Prioridad.valueOf(prioridad),
        estado = ReglasActividad.obtenerEstado(progreso, dias),
        horas = horas,
        enlaceEvidencia = enlaceEvidencia,
    )
}

fun ActividadFormativa.toEntity(competenciaId: Long): ActividadEntity {
    val hoy = LocalDate.now()
    val fechaLimite = hoy.plusDays(diasRestantes.toLong())
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    return ActividadEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        progreso = progreso,
        prioridad = prioridad.name,
        competenciaId = competenciaId,
        fechaLimiteEpochMillis = fechaLimite,
        completada = progreso >= 100,
        horas = horas,
        enlaceEvidencia = enlaceEvidencia,
    )
}

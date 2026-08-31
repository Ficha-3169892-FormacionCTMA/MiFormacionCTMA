package com.example.miformacionctma.data

import kotlinx.coroutines.flow.Flow

class ActividadRepository(private val actividadDao: ActividadDao) {
    val allActividades: Flow<List<Actividad>> = actividadDao.getAllActividades()

    suspend fun getActividadById(id: Int): Actividad? = actividadDao.getActividadById(id)

    suspend fun insertActividad(actividad: Actividad) = actividadDao.insertActividad(actividad)

    suspend fun updateActividad(actividad: Actividad) = actividadDao.updateActividad(actividad)

    suspend fun deleteActividad(actividad: Actividad) = actividadDao.deleteActividad(actividad)
}

package com.example.miformacionctma

import android.app.Application
import com.example.miformacionctma.data.local.database.FormacionDatabase
import com.example.miformacionctma.data.local.entities.CompetenciaEntity
import com.example.miformacionctma.data.repository.DataStorePreferenciasRepository
import com.example.miformacionctma.data.repository.RoomActividadRepository
import com.example.miformacionctma.data.repository.dataStore
import com.example.miformacionctma.data.repository.toEntity
import com.example.miformacionctma.domain.MockData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ActividadesApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // Contenedor manual para inyección de dependencias simple
    val database: FormacionDatabase by lazy { FormacionDatabase.getDatabase(this) }
    
    val actividadRepository: RoomActividadRepository by lazy {
        RoomActividadRepository(database.actividadDao())
    }
    
    val preferenciasRepository: DataStorePreferenciasRepository by lazy {
        DataStorePreferenciasRepository(dataStore)
    }

    override fun onCreate() {
        super.onCreate()
        prepoblarBaseDeDatos()
    }

    private fun prepoblarBaseDeDatos() {
        applicationScope.launch {
            val actividadesExistentes = actividadRepository.observarTodos().first()
            if (actividadesExistentes.isEmpty()) {
                // 1. Insertar competencia por defecto para cumplir con la FK
                database.competenciaDao().insertar(
                    CompetenciaEntity(id = 1L, nombre = "Formación Técnica")
                )

                // 2. Insertar las 10 actividades de MockData
                MockData.listaActividades.forEach { actividad ->
                    database.actividadDao().insertar(actividad.toEntity(competenciaId = 1L))
                }
            }
        }
    }
}

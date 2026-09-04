package com.example.miformacionctma.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.miformacionctma.domain.PreferenciasRepository
import com.example.miformacionctma.domain.PreferenciasUsuario
import com.example.miformacionctma.domain.Prioridad
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencias")

class DataStorePreferenciasRepository(private val dataStore: DataStore<Preferences>) : PreferenciasRepository {

    private object Keys {
        val filtroPrioridad = stringPreferencesKey("filtro_prioridad")
        val ordenadoPorVencimiento = booleanPreferencesKey("ordenado_por_vencimiento")
        val modoCuadricula = booleanPreferencesKey("modo_cuadricula")
    }

    override val preferencias: Flow<PreferenciasUsuario> = dataStore.data.map { p ->
        PreferenciasUsuario(
            filtroPrioridad = p[Keys.filtroPrioridad]?.let { Prioridad.valueOf(it) },
            ordenadoPorVencimiento = p[Keys.ordenadoPorVencimiento] ?: false,
            modoCuadricula = p[Keys.modoCuadricula] ?: false,
        )
    }

    override suspend fun guardarFiltroPrioridad(prioridad: Prioridad?) {
        dataStore.edit { p ->
            if (prioridad == null) {
                p.remove(Keys.filtroPrioridad)
            } else {
                p[Keys.filtroPrioridad] = prioridad.name
            }
        }
    }

    override suspend fun guardarOrdenadoPorVencimiento(ordenado: Boolean) {
        dataStore.edit { p ->
            p[Keys.ordenadoPorVencimiento] = ordenado
        }
    }

    override suspend fun guardarModoCuadricula(activo: Boolean) {
        dataStore.edit { p ->
            p[Keys.modoCuadricula] = activo
        }
    }
}

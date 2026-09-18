package com.example.miformacionctma

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.miformacionctma.data.local.database.FormacionDatabase
import com.example.miformacionctma.data.local.entities.CompetenciaEntity
import com.example.miformacionctma.data.remote.api.EvidenciaApi
import com.example.miformacionctma.data.repository.DataStorePreferenciasRepository
import com.example.miformacionctma.data.repository.DefaultEvidenciaRepository
import com.example.miformacionctma.data.repository.SyncedActividadRepository
import com.example.miformacionctma.data.repository.dataStore
import com.example.miformacionctma.data.repository.toEntity
import com.example.miformacionctma.domain.EvidenciaRepository
import com.example.miformacionctma.domain.MockData
import com.example.miformacionctma.worker.NotificacionWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ActividadesApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // Contenedor manual para inyección de dependencias simple
    val database: FormacionDatabase by lazy { FormacionDatabase.getDatabase(this) }
    
    val actividadRepository: SyncedActividadRepository by lazy {
        SyncedActividadRepository(database.actividadDao(), applicationScope)
    }
    
    val preferenciasRepository: DataStorePreferenciasRepository by lazy {
        DataStorePreferenciasRepository(dataStore)
    }

    // Semana 9: Configuración de Retrofit y EvidenciaRepository
    private val retrofit: Retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val evidenciaRepository: EvidenciaRepository by lazy {
        DefaultEvidenciaRepository(
            dao = database.evidenciaDao(),
            api = retrofit.create(EvidenciaApi::class.java),
            contentResolver = contentResolver
        )
    }

    override fun onCreate() {
        super.onCreate()
        rellenarDatosParaPrueba()
        iniciarNotificaciones()
    }

    private fun rellenarDatosParaPrueba() {
        applicationScope.launch {
            // Aseguramos que la competencia base exista
            database.competenciaDao().insertar(
                CompetenciaEntity(id = 1L, nombre = "Formación Técnica"),
            )

            val actividadesActuales = actividadRepository.observarTodos().first()
            
            // Si hay menos de 10, intentamos recuperar de la nube primero
            if (actividadesActuales.size < 10) {
                actividadRepository.sincronizarDesdeNube()
                
                // Volvemos a revisar tras la descarga
                val actividadesRecuperadas = actividadRepository.observarTodos().first()
                
                // Si tras la nube aún faltan (app nueva/sin internet), rellenamos con MockData
                if (actividadesRecuperadas.size < 10) {
                    MockData.listaActividades.forEach { actividad ->
                        val yaExiste = actividadesRecuperadas.any { it.titulo == actividad.titulo }
                        if (!yaExiste) {
                            database.actividadDao().insertar(actividad.toEntity(competenciaId = 1L))
                        }
                    }
                }
            }
        }
    }

    private fun iniciarNotificaciones() {
        val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "vencimiento_notificaciones",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
